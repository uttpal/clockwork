package com.uttpal.schedular.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Uttpal
 */
@Service
public class SchedulerPartitionService {
    private List<String> consumerPartitionMap;

    public SchedulerPartitionService() {
        this.consumerPartitionMap = new ArrayList<>();
    }

    public List<String> updatePartitions(List<String> partitions) {
        return partitions;
    }

    public List<String> getConsumerPartitionMap() {
        return consumerPartitionMap;
    }
}
