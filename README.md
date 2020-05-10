# Microservices using Vert.x

## Requirements

- Java 8
- Maven 3.3.9
- Postgres 12.2

## Run unit tests

### Project level
`mvn test`

### Module level
`mvn test -pl :module-artifact-name -am`

Eg: `mvn test -pl :account-service -am`

## How to run the application

### Compile
 `mvn clean package`

This will create jar files in each module (gateway, account etc.)

### Run DB migrations

#### Account service
`cd account-service`
`mvn -Dflyway.configFiles=src/main/resources/application.properties -Dflyway.schemas=userdb flyway:migrate`

### Run each services

#### Gateway
`java -Dvertx.hazelcast.config=cluster.xml -jar api-gateway/target/api-gateway-fat.jar -conf api-gateway/config/local.json -cluster`

#### Account
`java -Dvertx.hazelcast.config=cluster.xml -jar account-service/target/account-service-fat.jar -conf account-service/config/local.json -cluster`

### Check if microservices are working
`curl --request GET --url http://localhost:8786/api/account/users`
