package com.ratelimit.subscription.dto;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionCreatedEvent(UUID userId, UUID planId, Instant periodStart, Instant periodEnd) {}
