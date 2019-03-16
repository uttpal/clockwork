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
    Schedule updateStatus(String scheduleId, ScheduleStatus status, long version);
    public List<Schedule> scanSorted(String partitionId, long afterTime, long tillTime, int batchSize);
}
