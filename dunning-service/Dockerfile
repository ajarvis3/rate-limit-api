
# Runtime-only Dockerfile for dunning-service (Option B)
# Build the jar locally: .\gradlew :dunning-service:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=dunning-service/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


