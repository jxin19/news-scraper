FROM gradle:8.10-jdk-21-and-23-alpine AS build

WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle/

COPY core /app/core
COPY scrape-rss/build.gradle.kts /app/scrape-rss/
COPY scrape-rss/src /app/scrape-rss/src

RUN ./gradlew :scrape-rss:build -x test --no-daemon --stacktrace

FROM openjdk:21-slim

WORKDIR /app
COPY --from=build /app/scrape-rss/build/libs/*.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]
