FROM openjdk:11-jdk-slim
ARG JAR_FILE=inventi-task-0.1.0.jar
COPY ./build/libs/${JAR_FILE} /app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
EXPOSE 8080
