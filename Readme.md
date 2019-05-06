# Clockwork-Scheduler
Clockwork-Schedular is a general purpose distributed job scheduler. It offers you horizontally scalable scheduler with
atleast once delivery guarantees. Currently supported task delivery mechanism is kafka, at task execution time the schedule 
data is pushed to the given kafka topic.

It uses kafka for task buffering ordering, shard calculation and delivery. Currently DynamoDB 
is the only supported database.

## Getting Started
### Prerequisites
```java
java > 8
kafka > 0.11
DynamoDB
```

### Setup
* Create a dynamodb table called `partitionExecutions` with partitionKey `partitionId`(String) and no sort key
* Create a dynamodb table called `schedules` with partitionKey `partitionId`(String) and `scheduleTime`(Number)

* Set environment variables `KAFKA_BROKERS`, `KAFKA_CONSUMER_GROUP`, `AWS_REGION` `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, 
`SCHEDULE_DB` and `PARTITION_EXEC_DB`.

You can modify custom settings here `src/main/resources/application.properties`.

### Run
`mvn package` and `java -jar {package.jar}`