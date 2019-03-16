package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class PartitionScheduleMap {
    PartitionOffset partitionOffset;
    List<Schedule> schedules;
}