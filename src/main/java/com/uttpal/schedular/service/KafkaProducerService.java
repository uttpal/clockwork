package com.uttpal.schedular.service;

import com.uttpal.schedular.exception.KafkaProducerFailed;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Uttpal
 */
@Service
public class KafkaProducerService {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Retryable
    public String produce(String payload, String partitionKey, String topic) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partitionKey, payload);
        try {
            //TODO: add timeout to config
            future.get(10, TimeUnit.SECONDS);
            return partitionKey;
        } catch (TimeoutException | ExecutionException  e) {
            throw new KafkaProducerFailed(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaProducerFailed(e);
        }
    }
}
