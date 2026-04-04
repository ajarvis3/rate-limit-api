package com.ratelimit.user.dto;

// Moved to dto-common module
public record UserDTO(String keycloakId, java.time.Instant createdAt) {}

