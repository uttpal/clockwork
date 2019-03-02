package com.uttpal.schedular.service;

import com.uttpal.schedular.dao.ScheduleDao;
import com.uttpal.schedular.dao.ScheduleExecutionDao;
import com.uttpal.schedular.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * @author Uttpal
 */
@Service
public class ScheduleService {

    private ScheduleDao scheduleDao;
    private ScheduleExecutionDao scheduleExecutionDao;

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, ScheduleExecutionDao scheduleExecutionDao) {
        this.scheduleDao = scheduleDao;
        this.scheduleExecutionDao = scheduleExecutionDao;
    }

    public List<Schedule> executePartitions(List<String> partition) {
        partition.stream()
                .map(scheduleExecutionDao::get)
                .map(partitionOffset -> {
                    List<Schedule> s = scheduleDao.scan(partitionOffset.getPartitionId(), partitionOffset.getOffsetTimestamp(), Instant.now().toEpochMilli());

                })
    }

    private List<Schedule> executeSchedule(Schedule schedule) {
        schedule.getDelivery()
    }
}
