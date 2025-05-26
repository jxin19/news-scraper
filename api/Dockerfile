FROM gradle:8.10-jdk-21-and-23-alpine AS build

WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle/

COPY core /app/core
COPY api/build.gradle.kts /app/api/
COPY api/src /app/api/src

RUN ./gradlew :api:build -x test --no-daemon --stacktrace

FROM openjdk:21-slim

WORKDIR /app
COPY --from=build /app/api/build/libs/*.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]
