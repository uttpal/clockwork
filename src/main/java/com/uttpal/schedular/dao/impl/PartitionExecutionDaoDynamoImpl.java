package com.uttpal.schedular.dao.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;

import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.util.ImmutableMapParameter;
import com.google.gson.Gson;
import com.uttpal.schedular.aspect.NoLogging;
import com.uttpal.schedular.dao.PartitionExecutionDao;
import com.uttpal.schedular.exception.PartitionVersionMismatch;
import com.uttpal.schedular.model.PartitionOffset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author Uttpal
 */
@Component
public class PartitionExecutionDaoDynamoImpl implements PartitionExecutionDao {

    private AmazonDynamoDB dynamoDB;
    private String partitionExecutionTableName;
    private Gson gson;
    private String primaryKey = "partitionId";

    @Autowired
    public PartitionExecutionDaoDynamoImpl(AmazonDynamoDB dynamoDB, @Value("${dynamodb.table.name.partitionexecution}") String partitionExecutionTableName) {
        this.dynamoDB = dynamoDB;
        this.partitionExecutionTableName = partitionExecutionTableName;
        this.gson = new Gson();
    }

    @Override
    public PartitionOffset update(String partitionId, long updatedOffsetTimestamp, long currentVersion) throws PartitionVersionMismatch {
        UpdateItemRequest updateRequest = new UpdateItemRequest()
                .withTableName(partitionExecutionTableName)
                .withKey(ImmutableMapParameter.of(primaryKey, new AttributeValue(partitionId)))
                .withUpdateExpression("set #ver = #ver + :increment, #offset = :updatedOffset")
                .withExpressionAttributeValues(new ImmutableMapParameter.Builder<String, AttributeValue>()
                        .put(":increment", new AttributeValue().withN("" + 1))
                        .put(":currentVersion", new AttributeValue().withN("" + currentVersion))
                        .put(":updatedOffset", new AttributeValue().withN("" + updatedOffsetTimestamp))
                        .build()
                )
                .withExpressionAttributeNames(new ImmutableMapParameter.Builder<String, String>()
                    .put("#ver", "version")
                    .put("#offset", "offsetTimestamp")
                    .build()
                )
                .withConditionExpression("#ver = :currentVersion")
                .withReturnValues(ReturnValue.UPDATED_NEW);
        return attributeMapToPartitonOffset(dynamoDB.updateItem(updateRequest).getAttributes());
    }

    @Override
    public PartitionOffset updateVersion(String partitionId) {
        UpdateItemRequest updateRequest = new UpdateItemRequest()
                .withTableName(partitionExecutionTableName)
                .withKey(ImmutableMapParameter.of(primaryKey, new AttributeValue(partitionId)))
                .withUpdateExpression("set version = version + :val")
                .withExpressionAttributeValues(ImmutableMapParameter.of(":val", new AttributeValue().withN("" + 1)))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        return attributeMapToPartitonOffset(dynamoDB.updateItem(updateRequest).getAttributes());
    }

    @Override
    @NoLogging
    public PartitionOffset get(String partitionId) {
        ImmutableMapParameter<String, AttributeValue> key = new ImmutableMapParameter.Builder<String, AttributeValue>()
                .put(primaryKey, new AttributeValue(partitionId))
                .build();
        Map<String, AttributeValue> partitionOffsetMap = dynamoDB.getItem(partitionExecutionTableName, key).getItem();

        if(Objects.nonNull(partitionOffsetMap)) {
            return attributeMapToPartitonOffset(partitionOffsetMap);
        }

        //Create Partition Offset item
        PartitionOffset newPartition = PartitionOffset.newPartition(partitionId);
        Map<String, AttributeValue> partitionMap = ItemUtils.toAttributeValues(Item.fromJSON(gson.toJson(newPartition)));
        PutItemRequest insertItemRequest = new PutItemRequest()
                .withTableName(partitionExecutionTableName)
                .withItem(partitionMap)
                .withExpected(new ImmutableMapParameter.Builder<String, ExpectedAttributeValue>()
                        // When exists is false and the id already exists a ConditionalCheckFailedException will be thrown
                        .put(primaryKey, new ExpectedAttributeValue(false))
                        .build());
        try {
            dynamoDB.putItem(insertItemRequest);
            return newPartition;
        } catch (ConditionalCheckFailedException e) {
            //Item already exists retrieve it
            return get(partitionId);
        }
    }

    private PartitionOffset attributeMapToPartitonOffset(Map<String, AttributeValue> partitionOffsetMap) {
        String partitionOffsetJson = ItemUtils.toItem(partitionOffsetMap).toJSON();
        return gson.fromJson(partitionOffsetJson, PartitionOffset.class);
    }
}
