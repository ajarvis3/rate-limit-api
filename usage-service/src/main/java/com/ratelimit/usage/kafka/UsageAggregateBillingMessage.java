package com.ratelimit.usage.kafka;

import java.time.Instant;

public record UsageAggregateBillingMessage(
        String userId,
        Long requestCount,
        Instant lastUpdated,
        Instant periodStart,
        Instant periodEnd) {
}
