FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/monitor-0.0.1-SNAPSHOT.jar app.jar

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
