FROM gradle:8.10-jdk-21-and-23-alpine AS build

WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle/

COPY core /app/core
COPY scrape-web/build.gradle.kts /app/scrape-web/
COPY scrape-web/src /app/scrape-web/src

RUN ./gradlew :scrape-web:build -x test --no-daemon --stacktrace

FROM openjdk:21-slim

WORKDIR /app
COPY --from=build /app/scrape-web/build/libs/*.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]
