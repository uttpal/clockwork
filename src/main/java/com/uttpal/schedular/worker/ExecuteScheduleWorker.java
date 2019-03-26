package com.uttpal.schedular.worker;

import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.service.ScheduleService;
import com.uttpal.schedular.service.SchedulerPartitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Uttpal
 */
@Component
public class ExecuteScheduleWorker {

    private SchedulerPartitionService schedulerPartitionService;
    private ScheduleService scheduleService;

    @Autowired
    public ExecuteScheduleWorker(SchedulerPartitionService schedulerPartitionService, ScheduleService scheduleService) {
        this.schedulerPartitionService = schedulerPartitionService;
        this.scheduleService = scheduleService;
    }

    @NoLogging
    @Scheduled(fixedRate = 100)
    public void executeSchedules() {
        List<String> partitions = schedulerPartitionService.getConsumerPartitionList();
        scheduleService.executePartitions(partitions);
    }
}
