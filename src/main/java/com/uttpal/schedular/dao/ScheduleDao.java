package com.uttpal.schedular.dao;

import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.model.Schedule;

import java.util.List;
import java.util.Optional;

/**
 * @author Uttpal
 */
public interface ScheduleDao {
    Schedule create(Schedule schedule) throws EntityAlreadyExists;

    List<Schedule> batchDeleteSchedules(List<Schedule> schedules);

    List<Schedule> batchCreateExecuted(List<Schedule> schedules);


    Schedule get(String partitionId, long scheduleTime);
    Optional<Schedule> getByScheduleKey(String scheduleKey);

    @NoLogging
    List<Schedule> scanSorted(String partitionId, long currentTime, int batchSize);
}
