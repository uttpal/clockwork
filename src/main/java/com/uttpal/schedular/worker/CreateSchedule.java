package com.uttpal.schedular.worker;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Uttpal
 */
@Component
public class CreateSchedule {

    @KafkaListener(topics = "${kafka.create.topic}")
    public void processMessage(String message,
                               @Header(KafkaHeaders.PARTITION_ID) Integer partitions,
                               @Header(KafkaHeaders.TOPIC) String topic,
                               @Header(KafkaHeaders.OFFSET) Long offset) {


    }
}
