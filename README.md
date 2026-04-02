# rate-limit-api

This is a test microservice app. It will use at least kubernetes and redis.

Microservices that will be included:

api-gateway
Entry point for all external requests. Validates JWTs via Keycloak, enforces rate limits via Redis sliding window, stamps X-User-Id header, and routes requests to the correct downstream service.
hello-api
The api that will be rate limited.
user-service
Minimal. Stores a User entity that just holds a keycloakId and createdAt. Exists purely to give other services something to tie their data to. Created lazily on first authenticated request or via a Keycloak webhook on user registration.
subscription-service
Manages plans and the relationship between users and plans. Handles subscription lifecycle (create, upgrade, downgrade, cancel), billing period tracking, and status transitions. Notifies Keycloak when a plan changes so the JWT reflects the new role.
usage-service
Records individual API usage events and maintains running aggregates per user per billing period. Runs an incremental scheduled job that rolls new events into UsageAggregate using lastUpdatedAt. Marks aggregates as FINALIZED at period end.
billing-service
At period end reads the finalized UsageAggregate, applies tiered pricing, generates an Invoice, and "triggers payment". Creates PaymentAttempt records for each attempt.
dunning-service
Handles failed payment recovery. Retries failed payments on a schedule, sends reminders between retries, tracks attempt count against a max, and suspends or cancels the subscription after retries are exhausted.