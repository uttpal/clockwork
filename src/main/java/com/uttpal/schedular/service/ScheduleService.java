package com.uttpal.schedular.service;

import com.uttpal.schedular.dao.ScheduleDao;
import com.uttpal.schedular.dao.ScheduleExecutionDao;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.exception.PartitionVersionMismatch;
import com.uttpal.schedular.model.Delivery;
import com.uttpal.schedular.model.PartitionOffset;
import com.uttpal.schedular.model.PartitionScheduleMap;
import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.utils.DateTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @author Uttpal
 */
@Service
public class ScheduleService {

    private ScheduleDao scheduleDao;
    private ScheduleExecutionDao scheduleExecutionDao;
    private KafkaProducerService kafkaProducerService;
    private DateTimeUtil dateTimeUtil;
    private long DELAY_THRESHOLD_SEC;
    private long MISFIRE_THRESHOLD_SEC;
    private Logger logger = LogManager.getLogger(ScheduleService.class);

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, ScheduleExecutionDao scheduleExecutionDao, KafkaProducerService kafkaProducerService, DateTimeUtil dateTimeUtil, @Value("${schedule.delay.threshold.sec}") long DELAY_THRESHOLD_SEC, @Value("${schedule.misfire.threshold.sec}"), long MISFIRE_THRESHOLD_SEC) {
        this.scheduleDao = scheduleDao;
        this.scheduleExecutionDao = scheduleExecutionDao;
        this.kafkaProducerService = kafkaProducerService;
        this.dateTimeUtil = dateTimeUtil;
        this.DELAY_THRESHOLD_SEC = DELAY_THRESHOLD_SEC;
        this.MISFIRE_THRESHOLD_SEC = MISFIRE_THRESHOLD_SEC;
    }

    public Schedule create(Schedule schedule) throws EntityAlreadyExists {
        if(schedule.getScheduleTime() < (dateTimeUtil.getEpochSecs() + DELAY_THRESHOLD_SEC)) {
           execute(schedule);
            Schedule updatedSchedule = schedule.completeSchedule();
            return scheduleDao.create(updatedSchedule);
        }
        if(schedule.getScheduleTime() < (dateTimeUtil.getEpochSecs() + MISFIRE_THRESHOLD_SEC)) {
            scheduleExecutionDao.updateVersion(schedule.getPartitionId());
        }
        return scheduleDao.create(schedule);
    }

    public void executePartitions(List<String> partitions) {
        partitions.stream()
                .map(scheduleExecutionDao::get)
                .map(partitionOffset -> {
                    List<Schedule> schedules = scheduleDao.scanSorted(partitionOffset.getPartitionId(), partitionOffset.getOffsetTimestamp(), Instant.now().toEpochMilli(), 1);
                    return new PartitionScheduleMap(partitionOffset, schedules);
                })
                .map(this::executeSchedules)
                .forEach(this::commitPartitionSchedule);
    }

    private PartitionScheduleMap executeSchedules(PartitionScheduleMap partitionScheduleMap) {
        partitionScheduleMap.getSchedules()
                .forEach(this::execute);
        return partitionScheduleMap;
    }

    private Schedule execute(Schedule schedule) {
        Delivery delivery = schedule.getDelivery();
        if(Objects.nonNull(delivery.getTopic())) {
            kafkaProducerService.produce(schedule.getTaskData(), schedule.getOrderingKey(), delivery.getTopic());
        }
        //TODO:: add rest support
        logger.info("Successfully executed schedule {}", schedule);
        return schedule;
    }

    private PartitionScheduleMap commitPartitionSchedule(PartitionScheduleMap partitionScheduleMap) {
        List<Schedule> schedules = partitionScheduleMap.getSchedules();
        long updatedOffsetTime = schedules.get(schedules.size() - 1).getScheduleTime();

        PartitionOffset partitionOffset = partitionScheduleMap.getPartitionOffset();

        try {
            scheduleExecutionDao.update(partitionOffset.getPartitionId(), updatedOffsetTime, partitionOffset.getVersion());
            logger.info("Successfully Commited batch {}", partitionScheduleMap);
        } catch (PartitionVersionMismatch partitionVersionMismatch) {
            logger.info("Failed Committing schedule offset batch will be retried {} {}" , partitionScheduleMap, partitionVersionMismatch);
        }
        return partitionScheduleMap;
    }



}
