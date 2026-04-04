package com.ratelimit.billing.kafka;

import java.time.Instant;

public record UsageAggregateBillingMessage(
        String userId,
        Long requestCount,
        Instant lastUpdated,
        Instant periodStart,
        Instant periodEnd) {
}
