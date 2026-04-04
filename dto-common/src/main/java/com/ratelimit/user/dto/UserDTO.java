package com.ratelimit.user.dto;

import java.time.Instant;

public record UserDTO(String keycloakId, Instant createdAt) {}

