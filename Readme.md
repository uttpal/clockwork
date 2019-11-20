# Clockwork-Scheduler
Clockwork-Scheduler is a general purpose distributed job scheduler. It offers you horizontally scalable scheduler with
atleast once delivery guarantees. Currently supported task delivery mechanism is kafka, at task execution time the schedule 
data is pushed to the given kafka topic.

It uses kafka for task buffering ordering, shard calculation and delivery. Currently DynamoDB 
is the only supported database.

## Architecture
### Find more on blog [here](https://cynic.dev/posts/clockwork-scalable-job-scheduler/)
![architecture](https://cynic.dev/clockwork/Clockwork-aa135eaf-d958-42d7-a0f1-9db6031cff8c.jpg)
## Getting Started
### Prerequisites
```java
java > 8
kafka > 0.11
DynamoDB
```

### Setup
* Create a dynamodb table called `partitionExecutions` with partitionKey `partitionId`(String) and no sort key.
* Create a dynamodb table called `schedules` with partitionKey `partitionId`(String) and `scheduleTime`(Number).
* (Optional) if you will be using cancel api then create a GSI on table called `scheduleKey-index`.

* Set environment variables `KAFKA_BROKERS`, `KAFKA_CONSUMER_GROUP`, `AWS_REGION` `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, 
`SCHEDULE_DB` and `PARTITION_EXEC_DB`.

You can modify custom settings here `src/main/resources/application.properties`.

### Run
`mvn package` and `java -jar {package.jar}`

### Usage 
Create schedule`POST /schedule` 
```json
{
	"clientId": "client1",
	"scheduleKey": "order1-create",
	"orderingKey": "order1",
	"taskData": "payload",
	"delivery": {
		"topic": "orderTopic"
	},
	"scheduleTime": 1553629195570
}
``` 
`clientId`: field to identify clients in multi client deployment  
`scheduleId`: unique key for schedule (clientId + scheduleId should be unique  
`orderingKey`: (to guarantee ordering among schedule) all schedule with same ordering key will be delivered in order
`taskData`: payload you want to receive when schedule fires
`topic`: kafka topic in which you want to receive event
`scheduleTime`: schedule time in epoch 

### TODO
* Support REST callback 
* Support Cassandra store

### Advanced
##### Kafka
To customise kafka connection settings eg. authentication use modify  `config/KafkaConfig` 
##### Logging
All private methods are logged by default by aspect logger in `/aspect`, 
You can disable logging on particular method by adding `@NoLogging` to it or remove `PublicMethodLogging` to remove all logging

##### Metrics
Micrometer is used to push execution and creation metrics to elasticsearch to publish dashboards
configure `management.metrics.export.elastic.*` properties to set it up

##### Executor
`backoffsleeptime.sec` time to sleep if nothing to execute after number(5) of retries


