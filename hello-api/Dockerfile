
# Runtime-only Dockerfile for hello-api (Option B)
# Build the jar locally: .\gradlew :hello-api:bootJar
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=hello-api/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

