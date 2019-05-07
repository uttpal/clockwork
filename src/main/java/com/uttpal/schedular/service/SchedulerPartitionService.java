package com.uttpal.schedular.service;

import com.uttpal.schedular.aspect.NoLogging;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Uttpal
 */
@Service
public class SchedulerPartitionService {
    private ConcurrentHashMap<String, List<String>> consumerPartition;

    public SchedulerPartitionService() {
        this.consumerPartition = new ConcurrentHashMap<>();
    }

    public List<String> updatePartitions(String consumerId, List<String> partitions) {
        consumerPartition.put(consumerId, partitions);
        return partitions;
    }

    @NoLogging
    public List<String> getConsumerPartitionList() {
        return consumerPartition.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
