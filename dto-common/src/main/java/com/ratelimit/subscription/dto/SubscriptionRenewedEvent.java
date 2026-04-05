package com.ratelimit.subscription.dto;

import java.util.UUID;

public record SubscriptionRenewedEvent(UUID userId, UUID subscriptionId) {}
