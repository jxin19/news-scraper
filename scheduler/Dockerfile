FROM gradle:8.10-jdk-21-and-23-alpine AS build

WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle/

COPY core /app/core
COPY scheduler/build.gradle.kts /app/scheduler/
COPY scheduler/src /app/scheduler/src

RUN ./gradlew :scheduler:build -x test --no-daemon --stacktrace

FROM openjdk:21-slim

WORKDIR /app
COPY --from=build /app/scheduler/build/libs/*.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]
