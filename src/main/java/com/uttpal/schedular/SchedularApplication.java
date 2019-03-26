package com.uttpal.schedular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedularApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedularApplication.class, args);
    }

}
