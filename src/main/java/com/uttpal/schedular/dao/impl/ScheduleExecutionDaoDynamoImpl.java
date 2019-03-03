package com.uttpal.schedular.dao.impl;

import com.uttpal.schedular.dao.ScheduleExecutionDao;
import com.uttpal.schedular.model.PartitionOffset;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author Uttpal
 */
@Component
public class ScheduleExecutionDaoDynamoImpl implements ScheduleExecutionDao {

    @Override
    public PartitionOffset upsert(String partitionId, long updatedOffsetTimestamp, long currentOffsetTimestamp) {
        return null;
    }

    @Override
    public PartitionOffset get(String partitionId) {
        return null;
    }
}
