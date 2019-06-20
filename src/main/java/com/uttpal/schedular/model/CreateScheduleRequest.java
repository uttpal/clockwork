package com.uttpal.schedular.model;

import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @author Uttpal
 */
@ToString
@Getter
public class CreateScheduleRequest {
    @NotEmpty
    String clientId;

    @NotEmpty
    String scheduleKey;
    @NotEmpty
    String orderingKey;
    @NotEmpty
    String taskData;

    @Valid
    Delivery delivery;

    long scheduleTime;
}
