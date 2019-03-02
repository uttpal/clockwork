package com.uttpal.schedular.config;

import com.uttpal.schedular.service.KafkaRebalanceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

/**
 * @author Uttpal
 */
@EnableKafka
public class KafkaConfig {

    private KafkaRebalanceListener kafkaRebalanceListener;

    @Autowired
    public KafkaConfig(KafkaRebalanceListener kafkaRebalanceListener) {
        this.kafkaRebalanceListener = kafkaRebalanceListener;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            KafkaTemplate<Object, Object> template) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setErrorHandler(new SeekToCurrentErrorHandler(
                new DeadLetterPublishingRecoverer(template), 3));
        factory.getContainerProperties().setConsumerRebalanceListener(kafkaRebalanceListener);
        //TODO: enable concurrency
        factory.setConcurrency(1);

        return factory;
    }
}
