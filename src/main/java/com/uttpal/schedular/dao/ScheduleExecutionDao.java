package com.uttpal.schedular.dao;

import com.uttpal.schedular.model.PartitionOffset;

/**
 * @author Uttpal
 */
public interface ScheduleExecutionDao {
    PartitionOffset upsert(String partitionId, long updatedOffsetTimestamp, long currentOffsetTimestamp);
    PartitionOffset get(String partitionId);
}
