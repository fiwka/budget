# syntax = docker/dockerfile:1.4
FROM eclipse-temurin:25-jdk-alpine AS builder
ARG MODULE
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
RUN chmod +x gradlew

COPY build.gradle.kts* build.gradle* settings.gradle.kts* settings.gradle* gradle.properties* ./
COPY . .

RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew dependencies --no-daemon

RUN --mount=type=cache,target=/root/.gradle/caches \
    if [ -n "$MODULE" ]; then \
        ./gradlew :$MODULE:bootJar --no-daemon; \
    else \
        ./gradlew bootJar --no-daemon; \
    fi

FROM eclipse-temurin:25-jre-alpine
ARG MODULE
WORKDIR /app
COPY --from=builder /app/${MODULE:+$MODULE/}build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]