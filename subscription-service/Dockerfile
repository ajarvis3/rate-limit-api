
# Runtime-only Dockerfile for subscription-service (Option B)
# Build the jar locally: .\gradlew :subscription-service:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=subscription-service/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

