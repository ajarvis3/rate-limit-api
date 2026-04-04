package com.ratelimit.subscription.dto;

import java.time.Instant;

public record SubscriptionBillingDTO (
        String userId,
        Long requestCount,
        Instant lastUpdated,
        Instant periodStart,
        Instant periodEnd,
        String subscription
){
}
