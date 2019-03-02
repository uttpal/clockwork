package com.uttpal.schedular.dao;

import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.model.ScheduleExecution;

import java.util.List;

/**
 * @author Uttpal
 */
public interface ScheduleExecutionDao {
    long upsert(String partitionId, long offsetTimestamp);
    long get(String partitionId);
}
