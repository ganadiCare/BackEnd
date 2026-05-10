FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]