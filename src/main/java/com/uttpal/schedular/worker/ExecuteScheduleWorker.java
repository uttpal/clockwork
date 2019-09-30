package com.uttpal.schedular.worker;

import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.model.PartitionScheduleMap;
import com.uttpal.schedular.service.ScheduleService;
import com.uttpal.schedular.service.SchedulerPartitionService;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Uttpal
 */
@Component
public class ExecuteScheduleWorker implements CommandLineRunner {

    private Logger logger = LogManager.getLogger(ExecuteScheduleWorker.class);

    private final SchedulerPartitionService schedulerPartitionService;
    private final ScheduleService scheduleService;
    private final int backoffThreshold = 5;
    private final Integer backoffSleepTimeSec;

    @Autowired
    public ExecuteScheduleWorker(SchedulerPartitionService schedulerPartitionService, ScheduleService scheduleService, @Value("${backoffsleeptime.sec}") Integer backoffSleepTimeSec) {
        this.schedulerPartitionService = schedulerPartitionService;
        this.scheduleService = scheduleService;
        this.backoffSleepTimeSec = backoffSleepTimeSec;
    }




    @NoLogging
    @Override
    public void run(String... args) {
        int emptyScheduleBackoffCountDown = backoffThreshold;
        while (true) {
            try {
                List<String> partitions = schedulerPartitionService.getConsumerPartitionList();
                List<PartitionScheduleMap> executions = partitions.parallelStream()
                        .map(scheduleService::excecutePartitionSchedule)
                        .map(this::getFromCompleteableFuture)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                if(executions.isEmpty()) {
                    emptyScheduleBackoffCountDown = (emptyScheduleBackoffCountDown == 0) ? 0 : emptyScheduleBackoffCountDown - 1;
                } else {
                    emptyScheduleBackoffCountDown = backoffThreshold;
                }

                if(emptyScheduleBackoffCountDown == 0) {
                    Thread.sleep(backoffSleepTimeSec * 1000);
                }
            } catch (Exception e) {
                logger.error("Error while executing schedules", e);
            }
        }

    }

    @SneakyThrows
    private <T> T getFromCompleteableFuture(CompletableFuture<T> future) {
        return future.get();
    }
}
