package com.ratelimit.dunning.dto;

public record FailedBillingEvent(
        String userId,
        Long invoiceId,
        Double amount,
        long failedAt
) {}
