package com.ratelimit.dunning.dto;

// Moved to dto-common module; shim kept for compatibility
public record FailedBillingEvent(String userId, Long invoiceId, java.math.BigDecimal amount, long failedAt) {}
