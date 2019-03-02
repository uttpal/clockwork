package com.uttpal.schedular.model;

import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
public class CreateScheduleRequest {
    String clientId;
    String orderingKey;
    String uniquenessKey;
    String taskData;
    long scheduleTime;
}
