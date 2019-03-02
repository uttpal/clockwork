package com.uttpal.schedular.service;

import com.uttpal.schedular.dao.ScheduleDao;
import com.uttpal.schedular.dao.ScheduleExecutionDao;
import com.uttpal.schedular.model.Delivery;
import com.uttpal.schedular.model.PartitionOffset;
import com.uttpal.schedular.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Uttpal
 */
@Service
public class ScheduleService {

    private ScheduleDao scheduleDao;
    private ScheduleExecutionDao scheduleExecutionDao;
    private KafkaProducerService kafkaProducerService;

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, ScheduleExecutionDao scheduleExecutionDao, KafkaProducerService kafkaProducerService) {
        this.scheduleDao = scheduleDao;
        this.scheduleExecutionDao = scheduleExecutionDao;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<Schedule> executePartitions(List<String> partition) {
        partition.stream()
                .map(scheduleExecutionDao::get)
                .map(partitionOffset -> {
                    List<Schedule> schedules = scheduleDao.scanSorted(partitionOffset.getPartitionId(), partitionOffset.getOffsetTimestamp(), Instant.now().toEpochMilli(), 10);
                    return new PartitionScheduleMap(partitionOffset, schedules);
                })
                .map(this::executeSchedules)
                .map(this::commitPartitionSchedule)
                .collect(Collectors.toList());
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
        return schedule;
    }

    private PartitionScheduleMap commitPartitionSchedule(PartitionScheduleMap partitionScheduleMap) {
        List<Schedule> schedules = partitionScheduleMap.getSchedules();
        long updatedOffsetTime = schedules.get(schedules.size() - 1).getScheduleTime();

        String partitionId = partitionScheduleMap.getPartitionOffset().getPartitionId();
        long currentOffsetTime = partitionScheduleMap.getPartitionOffset().getOffsetTimestamp();

        scheduleExecutionDao.upsert(partitionId, updatedOffsetTime, currentOffsetTime);
        return partitionScheduleMap;
    }


    private class PartitionScheduleMap {
        PartitionOffset partitionOffset;
        List<Schedule> schedules;

        public PartitionScheduleMap(PartitionOffset partitionOffset, List<Schedule> schedules) {
            this.partitionOffset = partitionOffset;
            this.schedules = schedules;
        }

        public PartitionOffset getPartitionOffset() {
            return partitionOffset;
        }

        public List<Schedule> getSchedules() {
            return schedules;
        }
    }
}
