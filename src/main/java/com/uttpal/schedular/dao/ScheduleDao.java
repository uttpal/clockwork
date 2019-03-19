package com.uttpal.schedular.dao;

import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.model.ScheduleStatus;

import java.util.List;

/**
 * @author Uttpal
 */
public interface ScheduleDao {
    Schedule create(Schedule schedule) throws EntityAlreadyExists;

    Schedule get(String partitionId, long scheduleTime);

    Schedule updateStatus(String partitiionId, long scheduleTime, ScheduleStatus status, long fireTime, long version);

    List<Schedule> scanSorted(String partitionId, long afterTime, long tillTime, int batchSize);
}
