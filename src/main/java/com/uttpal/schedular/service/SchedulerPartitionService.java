package com.uttpal.schedular.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Uttpal
 */
@Service
public class SchedulerPartitionService {
    private List<String> consumerPartitionList;

    public SchedulerPartitionService() {
        this.consumerPartitionList = new ArrayList<>();
    }

    public List<String> updatePartitions(List<String> partitions) {
        consumerPartitionList = partitions;
        return partitions;
    }

    public List<String> getConsumerPartitionList() {
        return consumerPartitionList;
    }
}
