package com.uttpal.schedular.config;

import com.uttpal.schedular.service.KafkaRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Uttpal
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    private KafkaRebalanceListener kafkaRebalanceListener;
    private String bootstrapServers;
    private String consumerGroup;
    private String offsetResetPolicy;


    @Autowired
    public KafkaConfig(KafkaRebalanceListener kafkaRebalanceListener,@Value("${kafka.bootstrap.servers}") String bootstrapServers, @Value("${kafka.consumer.group-id}") String consumerGroup, @Value("${kafka.consumer.auto-offset-reset}") String offsetResetPolicy) {
        this.kafkaRebalanceListener = kafkaRebalanceListener;
        this.bootstrapServers = bootstrapServers;
        this.consumerGroup = consumerGroup;
        this.offsetResetPolicy = offsetResetPolicy;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(KafkaTemplate<String, String> template) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setSyncCommits(true);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setSyncCommits(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setErrorHandler(new SeekToCurrentErrorHandler());
        factory.getContainerProperties().setConsumerRebalanceListener(kafkaRebalanceListener);
        //TODO: enable concurrency
        factory.setConcurrency(1);
        return factory;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetPolicy);

////        SASL
//        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
//        String jaasCfg = String.format(jaasTemplate, "qsbomzuj", "9eA0R-MijhgPgEnjKUR4762rDx6wwocw");
//
//        config.put("security.protocol", "SASL_SSL");
//        config.put("sasl.mechanism", "SCRAM-SHA-256");
//        config.put("sasl.jaas.config", jaasCfg);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new StringSerializer());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

//        //Sasl
//        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
//        String jaasCfg = String.format(jaasTemplate, "qsbomzuj", "9eA0R-MijhgPgEnjKUR4762rDx6wwocw");
//
//        props.put("security.protocol", "SASL_SSL");
//        props.put("sasl.mechanism", "SCRAM-SHA-256");
//        props.put("sasl.jaas.config", jaasCfg);
        return props;
    }
}
