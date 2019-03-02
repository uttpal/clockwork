package com.uttpal.schedular.exception;

/**
 * @author Uttpal
 */
public class KafkaProducerFailed extends RuntimeException {
    public KafkaProducerFailed(Exception e) {
        super(e);
    }
}
