package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * @author Uttpal
 */
@ToString
@AllArgsConstructor
@Getter
public class Schedule {
    String clientId;
    String partitionId;
    String scheduleKey;
    String orderingKey;
    String taskData;
    Delivery delivery;
    ScheduleStatus status;
    long scheduleTime;
    long executionTime;
    long enqueTime;
    long version;

    private Schedule copy() {
        return new Schedule(clientId, partitionId, scheduleKey, orderingKey, taskData, delivery, status, scheduleTime, executionTime, enqueTime, version);
    }

    public Schedule completeSchedule() {
        Schedule updateSchedule = copy();
        updateSchedule.status = ScheduleStatus.EXECUTED;
        updateSchedule.executionTime = Instant.now().toEpochMilli();
        return updateSchedule;
    }

    public Schedule updateScheduleTime(long scheduleTime) {
        Schedule updateSchedule = copy();

        updateSchedule.scheduleTime = scheduleTime;
        return updateSchedule;
    }
}
