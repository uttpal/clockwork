package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@AllArgsConstructor
public class Schedule {
    String clientId;
    String partitionId;
    String orderingKey;
    String uniquenessKey;
    String taskData;
    long scheduleTime;
    long enqueTime;
}
