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
public class CancelScheduleRequest {
    @NotEmpty
    String clientId;

    @NotEmpty
    String scheduleKey;
}
