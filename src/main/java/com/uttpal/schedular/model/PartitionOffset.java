package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@AllArgsConstructor
@Getter
public class PartitionOffset {
    private String partitionId;
    private long offsetTimestamp;
    private long version;

    private PartitionOffset copy() {
        return new PartitionOffset(partitionId, offsetTimestamp, version);
    }
    public PartitionOffset updateOffsetAndVersion(long offsetTimestamp) {
        PartitionOffset partitionOffsetCopy = copy();
        partitionOffsetCopy.version = version + 1;
        partitionOffsetCopy.offsetTimestamp = offsetTimestamp;
        return partitionOffsetCopy;
    }
    public static PartitionOffset newPartition(String partitionId) {
        return new PartitionOffset(partitionId, 0, 1);
    }

}
