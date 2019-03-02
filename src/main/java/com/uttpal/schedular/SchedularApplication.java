package com.uttpal.schedular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;

@SpringBootApplication
public class SchedularApplication implements ApplicationListener<KafkaEvent> {

    public static void main(String[] args) {
        SpringApplication.run(SchedularApplication.class, args);
    }

    @Override
    public void onApplicationEvent(KafkaEvent event) {
        event.
    }

}
