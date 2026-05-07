FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]