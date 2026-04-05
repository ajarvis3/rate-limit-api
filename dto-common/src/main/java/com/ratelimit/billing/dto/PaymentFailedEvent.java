package com.ratelimit.billing.dto;

import java.util.UUID;

public record PaymentFailedEvent(UUID userId, UUID invoiceId, int attemptCount) {}
