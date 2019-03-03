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
public class Schedule {
    String clientId;
    String partitionId;
    String orderingKey;
    String uniquenessKey;
    String taskData;
    Delivery delivery;
    ScheduleStatus status;
    long scheduleTime;
    long enqueTime;
}
