package com.ratelimit.subscription.dto;

import java.util.UUID;

public record SubscriptionSuspendedEvent(UUID userId, UUID subscriptionId) {}
