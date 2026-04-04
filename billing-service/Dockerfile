
# Runtime-only Dockerfile for billing-service (Option B)
# Build the jar locally: .\gradlew :billing-service:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=billing-service/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


