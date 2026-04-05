package com.ratelimit.user.dto;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String keycloakId, String planName) {}
