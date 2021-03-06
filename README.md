# Hacker News Comment Processing with Kafka

![Architecture](./resources/Arch.png)

### Introduction

* This application collect Hacker News user comment from Internet. All comments will be joining with a Keyword Kafka table(changelog stream) to find the interesting information.
* For the Keyword changelog part, I use [Debezium Connector for PostgreSQL](https://debezium.io/documentation/reference/connectors/postgresql.html) to streaming the database change data events into the Kafka broker. Every keyword change in PostgreSQL table will reflect to the Kafka broker in real time.
* Spring Framework for serving the web user interface.

### Screenshots  

![Front Page](./resources/screenshot_1.png)

![Keywords](./resources/screenshot_2.png)

### Run Demo (From existing Docker Image on GitHub)

```shell
# Clone the project
git clone https://github.com/garyparrot/hackernews-comment-processing.git
cd hackernews-comment-processing

# launch docker-compose
cd ./demo-environment
docker-compose up

# visite the user interface
open http://localhost:8080
```

### Run Demo (Build everything manually)

```shell
# Clone the project
git clone https://github.com/garyparrot/hackernews-comment-processing.git
cd hackernews-comment-processing

# Download connectors
sh ./dev-support/pg-data-connector/download-connector.sh
sh ./dev-support/pg-cdc-connector/download-connector.sh

# Bootstrap building environment (we need schema-registry otherwise we can't build)
cd dev-support
docker-compose up -d
cd ../

# Build
mvn clean package

# Start full environment
cd dev-support
docker-compose stop
docker-compose -f docker-compose.application.yaml -f docker-compose.yaml up

# visite the user interface
open http://localhost:8080
```

### Related Tools in this Project

* Kafka Producer
* Kafka Consumer
* Kafka Stream
* Kafka Connect
  * debezium posgres connector
  * Jdbc Sink Connector
* Confluent Schema Registry
* Docker
* Spring Framework