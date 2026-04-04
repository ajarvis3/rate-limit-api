
# Runtime-only Dockerfile for usage-service (Option B)
# Build the jar locally: .\gradlew :usage-service:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=usage-service/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


