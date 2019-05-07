package com.uttpal.schedular.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Uttpal
 */
@Component
public class KafkaRebalanceListener implements ConsumerAwareRebalanceListener {

    private SchedulerPartitionService schedulerPartitionService;

    @Autowired
    public KafkaRebalanceListener(SchedulerPartitionService schedulerPartitionService) {
        this.schedulerPartitionService = schedulerPartitionService;
    }


    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        updateSchedulerExecutionPartitions(consumer);
    }

    @Override
    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        updateSchedulerExecutionPartitions(consumer);
    }

    @Override
    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        updateSchedulerExecutionPartitions(consumer);
    }

    private void updateSchedulerExecutionPartitions(Consumer<?, ?> consumer) {
        List<String> partitionIds = consumer.assignment()
                .stream()
                .map(TopicPartition::partition)
                .map(String::valueOf)
                .collect(Collectors.toList());
        schedulerPartitionService.updatePartitions(consumer.toString(), partitionIds);
    }


}
