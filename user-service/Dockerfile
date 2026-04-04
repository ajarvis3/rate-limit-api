
# Runtime-only Dockerfile for user-service (Option B)
# Build the jar locally: .\gradlew :user-service:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=user-service/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

