package com.uttpal.schedular.dao;

import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.model.ScheduleStatus;

import java.util.List;

/**
 * @author Uttpal
 */
public interface ScheduleDao {
    Schedule create(Schedule schedule) throws EntityAlreadyExists;

    List<Schedule> batchDeleteSchedules(List<Schedule> schedules);

    List<Schedule> batchCreateExecuted(List<Schedule> schedules);


    Schedule get(String partitionId, long scheduleTime);

    @NoLogging
    List<Schedule> scanSorted(String partitionId, long currentTime, int batchSize);
}
