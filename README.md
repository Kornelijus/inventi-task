# Bank Account Balance Management API

Take-Home Coding Challenge from Inventi

## Usage

```sh
./gradlew build && java -jar build/libs/inventi-task-0.1.0.jar
# or deploying with Docker
./gradlew build && docker-compose up -d
```

The server will be available at port [`8080`](http://localhost:8080)

## API Endpoints

Automatically generated API documentation (Swagger)
can be reached at [`/api/v1`](http://localhost:8080/api/v1).

Dates have to be formatted as `yyyy-MM-dd`,
currency as `ISO 421` currency codes,
transaction amounts have to be non-negative.
