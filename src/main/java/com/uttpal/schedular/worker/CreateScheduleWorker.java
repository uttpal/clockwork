package com.uttpal.schedular.worker;

import com.google.gson.Gson;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.model.CreateScheduleRequest;
import com.uttpal.schedular.model.Delivery;
import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.service.ScheduleService;
import com.uttpal.schedular.service.SchedulerPartitionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * @author Uttpal
 */
@Component
public class CreateScheduleWorker {

    private ScheduleService scheduleService;
    private Gson gson = new Gson();
    private Logger logger = LogManager.getLogger(CreateScheduleWorker.class);

    @Autowired
    public CreateScheduleWorker(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @KafkaListener(topics = "${schedule.create.kafka.topicName}")
    public void processMessage(String message,
                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                               Acknowledgment acknowledgment) {

        CreateScheduleRequest request = gson.fromJson(message, CreateScheduleRequest.class);
//        acknowledgment.acknowledge();
        try {
            scheduleService.create(Schedule.create(request.getClientId(), partition.toString(), request.getScheduleKey(), request.getOrderingKey(), request.getTaskData(), request.getDelivery(), request.getScheduleTime(), Instant.now().toEpochMilli()));
            acknowledgment.acknowledge();
        } catch (EntityAlreadyExists entityAlreadyExists) {
            logger.warn("Schedule Already Exists {}", request, entityAlreadyExists);
        }

    }
}
