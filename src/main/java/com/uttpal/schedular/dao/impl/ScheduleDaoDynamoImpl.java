package com.uttpal.schedular.dao.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.util.ImmutableMapParameter;
import com.google.gson.Gson;
import com.uttpal.schedular.dao.ScheduleDao;
import com.uttpal.schedular.exception.EntityAlreadyExists;
import com.uttpal.schedular.model.Schedule;
import com.uttpal.schedular.model.ScheduleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Uttpal
 */
@Component
public class ScheduleDaoDynamoImpl implements ScheduleDao {

    private AmazonDynamoDB dynamoDB;
    private String scheduleTableName;
    private String partitionKey = "partitionId";
    private String sortKey = "scheduleTime";
    private Gson gson = new Gson();

    @Autowired
    public ScheduleDaoDynamoImpl(AmazonDynamoDB dynamoDB, @Value("${dynamodb.table.name.schedules}") String scheduleTableName) {
        this.dynamoDB = dynamoDB;
        this.scheduleTableName = scheduleTableName;
    }

    @Override
    public Schedule create(Schedule schedule) throws EntityAlreadyExists {
        Map<String, AttributeValue> scheduleAttrMap = ItemUtils.toAttributeValues(Item.fromJSON(gson.toJson(schedule)));
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(scheduleTableName)
                .withItem(scheduleAttrMap)
                .withConditionExpression(String.format("attribute_not_exists(%s) AND attribute_not_exists(%s)", partitionKey, sortKey));
        try {
            Map<String, AttributeValue> createdScheduleAttrMap = dynamoDB.putItem(putItemRequest).getAttributes();
            return attributeMapToSchedule(createdScheduleAttrMap);

        } catch (ConditionalCheckFailedException e) {
            Schedule existingSchedule = get(schedule.getPartitionId(), schedule.getScheduleTime());
            if(existingSchedule.getScheduleKey().equals(schedule.getScheduleKey())) {
                throw new EntityAlreadyExists(String.format("Schedule %s already exists %s", schedule, existingSchedule), e);
            }
            Schedule delayedSchedule = addRandomDelayToSchedule(schedule);
            return create(delayedSchedule);
        }
    }

    @Override
    public Schedule get(String partitionId, long scheduleTime) {
        Map<String, AttributeValue> scheduleMap = dynamoDB.getItem(scheduleTableName, new ImmutableMapParameter.Builder<String, AttributeValue>()
                .put(partitionKey, new AttributeValue(partitionId))
                .put(sortKey, new AttributeValue().withN("" + scheduleTime))
                .build()
        ).getItem();
        return Objects.nonNull(scheduleMap) ? attributeMapToSchedule(scheduleMap) : null;
    }

    @Override
    public Schedule updateStatus(String partitionId, long scheduleTime, ScheduleStatus status, long fireTime, long version) {
        UpdateItemRequest updateRequest = new UpdateItemRequest()
                .withTableName(scheduleTableName)
                .withUpdateExpression("set #ver = #ver + :increment, #status = :status, #fireTime = :firetime")
                .withExpressionAttributeValues(new ImmutableMapParameter.Builder<String, AttributeValue>()
                        .put(":increment", new AttributeValue().withN("" + 1))
                        .put(":status", new AttributeValue(status.name()))
                        .put(":currentVersion", new AttributeValue().withN("" + version))
                        .put(":fireTime", new AttributeValue().withN("" + fireTime))
                        .build()
                )
                .withExpressionAttributeNames(new ImmutableMapParameter.Builder<String, String>()
                        .put("#ver", "version")
                        .put("#status", "status")
                        .put("#fireTime", "fireTime")
                        .build()
                )
                .withConditionExpression("#ver = :currentVersion")
                .withKey(new AbstractMap.SimpleEntry<>(partitionKey, new AttributeValue(partitionId)), new AbstractMap.SimpleEntry<>(sortKey, new AttributeValue().withN("" + scheduleTime)))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        return attributeMapToSchedule(dynamoDB.updateItem(updateRequest).getAttributes());
    }

    @Override
    public List<Schedule> scanSorted(String partitionId, long afterTime, long tillTime, int batchSize) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(scheduleTableName)
                .withConsistentRead(true)
                .withKeyConditionExpression("partitionId = :part AND scheduleTime BETWEEN :starttime AND :endtime")
                .withExpressionAttributeValues(new ImmutableMapParameter.Builder<String, AttributeValue>()
                        .put(":part", new AttributeValue(partitionId))
                        .put(":starttime", new AttributeValue().withN("" + afterTime))
                        .put(":endtime", new AttributeValue().withN("" + tillTime))
                        .build()
                )
                .withLimit(batchSize);

        List<Map<String, AttributeValue>> schdeuleMaps = dynamoDB.query(queryRequest).getItems();
        return schdeuleMaps.stream()
                .map(this::attributeMapToSchedule)
                .collect(Collectors.toList());
    }

    private Schedule attributeMapToSchedule(Map<String, AttributeValue> scheduleMap) {
        String partitionOffsetJson = ItemUtils.toItem(scheduleMap).toJSON();
        return gson.fromJson(partitionOffsetJson, Schedule.class);
    }

    private Schedule addRandomDelayToSchedule(Schedule schedule) {
        return schedule.updateScheduleTime(schedule.getScheduleTime() + new Random().ints(1, 1, 10000).findFirst().getAsInt());
    }
}
