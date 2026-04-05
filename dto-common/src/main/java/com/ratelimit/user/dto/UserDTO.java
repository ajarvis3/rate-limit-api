package com.ratelimit.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(UUID id, String keycloakId, Instant createdAt) {}

