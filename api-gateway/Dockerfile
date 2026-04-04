
# Runtime-only Dockerfile for api-gateway (Option B)
# Build the jar locally first with: .\gradlew :api-gateway:bootJar
FROM eclipse-temurin:21-jre-jammy

# The build must produce a jar at api-gateway/build/libs/*.jar before building this image
ARG JAR_FILE=api-gateway/build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

