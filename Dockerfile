ARG JAR_FILE=./build/libs/inventi-task-0.1.0-SNAPSHOT.jar

# This Build step takes way too long and doesn't seem to cache properly.
# It's better to build the project locally for now.
# FROM gradle:7.3.0-jdk11-alpine AS build
# COPY . .
# RUN gradle build

FROM openjdk:11-jdk-slim AS run
WORKDIR /app
COPY ${JAR_FILE} ./app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
EXPOSE 8080
