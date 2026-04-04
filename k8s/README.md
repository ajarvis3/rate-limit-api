This directory contains Kubernetes manifests and instructions to build Docker images for each microservice in this repository.

Quick build (from repository root on Windows cmd.exe):

1. Build images (uses Docker):

   gradlew :api-gateway:bootJar -x test --no-daemon
   docker build -t ratelimit/api-gateway:latest -f api-gateway/Dockerfile ./

   gradlew :billing-service:bootJar -x test --no-daemon
   docker build -t ratelimit/billing-service:latest -f billing-service/Dockerfile ./

   gradlew :dunning-service:bootJar -x test --no-daemon
   docker build -t ratelimit/dunning-service:latest -f dunning-service/Dockerfile ./

   gradlew :hello-api:bootJar -x test --no-daemon
   docker build -t ratelimit/hello-api:latest -f hello-api/Dockerfile ./

   gradlew :subscription-service:bootJar -x test --no-daemon
   docker build -t ratelimit/subscription-service:latest -f subscription-service/Dockerfile ./

   gradlew :usage-service:bootJar -x test --no-daemon
   docker build -t ratelimit/usage-service:latest -f usage-service/Dockerfile ./

   gradlew :user-service:bootJar -x test --no-daemon
   docker build -t ratelimit/user-service:latest -f user-service/Dockerfile ./

2. Deploy to Kubernetes (requires kubectl configured to your cluster):

   kubectl apply -f k8s/microservices-deployments.yaml

3. To delete the namespace and all resources created:

   kubectl delete namespace ratelimit

Notes:
- All services are exposed as ClusterIP on port 8080. Adjust manifests if you need NodePort/LoadBalancer.
- Images use a multi-stage Gradle build which requires Docker to have network access to download Gradle base images.
- If you prefer building with Gradle then using a lightweight runtime image (recommended for CI), run Gradle build and copy the generated jar into a small JRE image.

