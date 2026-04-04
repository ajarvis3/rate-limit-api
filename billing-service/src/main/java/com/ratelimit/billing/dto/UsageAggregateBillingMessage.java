package com.ratelimit.billing.dto;

// Moved to dto-common module; keep small shim while migrating
public record UsageAggregateBillingMessage(String userId, Long requestCount, java.time.Instant lastUpdated, java.time.Instant periodStart, java.time.Instant periodEnd) {}
