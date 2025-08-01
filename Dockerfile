# Stage 1: Build
FROM maven:3.9-eclipse-temurin-23 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:23-jre
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/target/*.jar app.jar

# Debug toggle
ARG ENABLE_DEBUG=false
ENV ENABLE_DEBUG=${ENABLE_DEBUG}

ENTRYPOINT sh -c "java $([ \"$ENABLE_DEBUG\" = \"true\" ] && echo '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005') -jar app.jar"
