package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class PartitionScheduleMap {
    String partition;
    List<Schedule> schedules;
    public boolean isNotEmpty() {
        return !this.schedules.isEmpty();
    }
}