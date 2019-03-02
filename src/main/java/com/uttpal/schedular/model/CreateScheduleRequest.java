package com.uttpal.schedular.model;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@Getter
public class CreateScheduleRequest {
    String clientId;
    String orderingKey;
    String uniquenessKey;
    String taskData;
    long scheduleTime;
}
