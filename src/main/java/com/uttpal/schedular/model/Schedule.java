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
    long version;

    private Schedule copy() {
        return new Schedule(clientId, partitionId,orderingKey, uniquenessKey, taskData, delivery, status, scheduleTime, enqueTime, version);
    }

    public Schedule completeSchedule() {
        Schedule updateSchedule = copy();
        updateSchedule.status = ScheduleStatus.EXECUTED;
        return updateSchedule;
    }
}
