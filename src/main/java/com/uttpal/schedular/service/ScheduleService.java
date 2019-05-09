package com.uttpal.schedular.service;

import com.google.gson.Gson;
import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.dao.ScheduleDao;
import com.uttpal.schedular.dao.PartitionExecutionDao;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.exception.PartitionVersionMismatch;
import com.uttpal.schedular.model.*;
import com.uttpal.schedular.utils.DateTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Uttpal
 */
@Service
public class ScheduleService {

    private ScheduleDao scheduleDao;
    private PartitionExecutionDao partitionExecutionDao;
    private KafkaProducerService kafkaProducerService;
    private DateTimeUtil dateTimeUtil;
    private long DELAY_THRESHOLD_SEC;
    private long MISFIRE_THRESHOLD_SEC;
    private String createScheduleTopic;
    private Logger logger = LogManager.getLogger(ScheduleService.class);
    private Gson gson = new Gson();

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, PartitionExecutionDao partitionExecutionDao,
                           KafkaProducerService kafkaProducerService, DateTimeUtil dateTimeUtil,
                           @Value("${schedule.delay.threshold.sec}") long DELAY_THRESHOLD_SEC,
                           @Value("${schedule.misfire.threshold.sec}") long MISFIRE_THRESHOLD_SEC,
                           @Value("${schedule.create.kafka.topicName}") String createScheduleTopic) {
        this.scheduleDao = scheduleDao;
        this.partitionExecutionDao = partitionExecutionDao;
        this.kafkaProducerService = kafkaProducerService;
        this.dateTimeUtil = dateTimeUtil;
        this.DELAY_THRESHOLD_SEC = DELAY_THRESHOLD_SEC;
        this.MISFIRE_THRESHOLD_SEC = MISFIRE_THRESHOLD_SEC;
        this.createScheduleTopic = createScheduleTopic;
    }

    public String schedule(CreateScheduleRequest createScheduleRequest) {
        return kafkaProducerService.produce(gson.toJson(createScheduleRequest), createScheduleRequest.getOrderingKey(), createScheduleTopic);
    }

    public Schedule create(Schedule schedule) throws EntityAlreadyExists {
        if(schedule.getScheduleTime() < (dateTimeUtil.getEpochMillis() + DELAY_THRESHOLD_SEC*1000)) {
            execute(schedule);
            Schedule updatedSchedule = schedule.completeSchedule(dateTimeUtil.getEpochMillis());
            return scheduleDao.create(updatedSchedule);
        }
        if(schedule.getScheduleTime() < (dateTimeUtil.getEpochMillis() + MISFIRE_THRESHOLD_SEC*1000)) {
            partitionExecutionDao.updateVersion(schedule.getPartitionId());
        }
        return scheduleDao.create(schedule);
    }

    @NoLogging
    public List<PartitionScheduleMap> executePartitions(List<String> partitions) {
        return partitions.stream()
                .map(partitionExecutionDao::get)
                .map(partitionOffset -> {
                    List<Schedule> schedules = scheduleDao.scanSorted(partitionOffset.getPartitionId(), partitionOffset.getOffsetTimestamp(), dateTimeUtil.getEpochMillis(), 100);
                    return new PartitionScheduleMap(partitionOffset, schedules);
                })
                .filter(PartitionScheduleMap::isNotEmpty)
                .map(this::executeSchedules)
                .map(this::commitPartitionSchedule)
                .collect(Collectors.toList());
    }

    @Async
    public CompletableFuture<List<PartitionScheduleMap>> excecutePartitionSchedule(String partition) {
        List<PartitionScheduleMap> executedSchedules = executePartitions(Collections.singletonList(partition));
        if(!executedSchedules.isEmpty()) {
            logger.info("SuccessFully Executed {}", executedSchedules);
        }
        return CompletableFuture.completedFuture(executedSchedules);
    }

    private PartitionScheduleMap executeSchedules(PartitionScheduleMap partitionScheduleMap) {
        partitionScheduleMap.getSchedules()
                .stream()
                .map(this::execute)
                .forEach(schedule -> scheduleDao.updateStatus(schedule.getPartitionId(), schedule.getScheduleTime(), ScheduleStatus.EXECUTED, dateTimeUtil.getEpochMillis(), schedule.getVersion()));
        return partitionScheduleMap;
    }

    private Schedule execute(Schedule schedule) {
        Delivery delivery = schedule.getDelivery();
        if(Objects.nonNull(delivery.getTopic())) {
            kafkaProducerService.produce(schedule.getTaskData(), schedule.getOrderingKey(), delivery.getTopic());
        }
        //TODO:: add rest support
        logger.info("Successfully executed schedule {} execution latency is {} ms", schedule, dateTimeUtil.getEpochMillis() - schedule.getScheduleTime());
        return schedule;
    }

    private PartitionScheduleMap commitPartitionSchedule(PartitionScheduleMap partitionScheduleMap) {
        List<Schedule> schedules = partitionScheduleMap.getSchedules();
        long updatedOffsetTime = schedules.get(schedules.size() - 1).getScheduleTime();

        PartitionOffset partitionOffset = partitionScheduleMap.getPartitionOffset();

        try {
            partitionExecutionDao.update(partitionOffset.getPartitionId(), updatedOffsetTime, partitionOffset.getVersion());
            logger.info("Successfully Commited batch {}", partitionScheduleMap);
        } catch (PartitionVersionMismatch partitionVersionMismatch) {
            logger.info("Failed Committing schedule offset batch will be retried {} {}" , partitionScheduleMap, partitionVersionMismatch);
        }
        return partitionScheduleMap;
    }



}
