package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

/**
 * @author Uttpal
 */
@ToString
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
    Long ttl;

    private Schedule(String clientId, String partitionId, String scheduleKey, String orderingKey, String taskData, Delivery delivery, ScheduleStatus status, long scheduleTime, long executionTime, long enqueTime, long version) {
        this.clientId = clientId;
        this.partitionId = partitionId;
        this.scheduleKey = scheduleKey;
        this.orderingKey = orderingKey;
        this.taskData = taskData;
        this.delivery = delivery;
        this.status = status;
        this.scheduleTime = scheduleTime;
        this.executionTime = executionTime;
        this.enqueTime = enqueTime;
        this.version = version;
    }
    public static Schedule create(String clientId, String partitionId, String scheduleKey, String orderingKey, String taskData, Delivery delivery, long scheduleTime, long enqueTime) {
        return new Schedule(clientId, partitionId, clientId + "-" +scheduleKey, orderingKey, taskData, delivery, ScheduleStatus.PENDING, scheduleTime, 0, enqueTime, 1);
    }

    private Schedule copy() {
        return new Schedule(clientId, partitionId, scheduleKey, orderingKey, taskData, delivery, status, scheduleTime, executionTime, enqueTime, version);
    }

    public Schedule completeSchedule(long currentTime, long ttl) {
        Schedule updateSchedule = copy();
        updateSchedule.status = ScheduleStatus.EXECUTED;
        updateSchedule.executionTime = currentTime;
        updateSchedule.ttl = ttl;
        return updateSchedule;
    }

    public Schedule updateScheduleTime(long scheduleTime) {
        Schedule updateSchedule = copy();

        updateSchedule.scheduleTime = scheduleTime;
        return updateSchedule;
    }

    public static boolean isValid(Schedule schedule) {
        return Objects.nonNull(schedule.getDelivery().getTopic()) && !schedule.getDelivery().getTopic().isEmpty();
    }
}
