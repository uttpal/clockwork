package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@AllArgsConstructor
public class ScheduleExecution {
    private String partitionId;
    private long offsetTimestamp;
}
