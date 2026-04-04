package com.ratelimit.billing.kafka;

import java.math.BigDecimal;

public record FailedBillingEvent(
        String userId,
        Long invoiceId,
        BigDecimal amount,
        long failedAt) {
}
