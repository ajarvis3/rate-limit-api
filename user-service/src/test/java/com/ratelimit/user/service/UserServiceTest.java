package com.ratelimit.user.service;

import com.ratelimit.user.dto.UserDTO;
import com.ratelimit.user.model.User;
import com.ratelimit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void findOrCreateByKeycloakId_returnsExistingUser() {
        User existing = new User("kc-1", Instant.parse("2020-01-01T00:00:00Z"));
        when(userRepository.findByKeycloakId("kc-1")).thenReturn(Optional.of(existing));

        UserDTO dto = userService.findOrCreateByKeycloakId("kc-1");

        assertThat(dto.keycloakId()).isEqualTo("kc-1");
        assertThat(dto.createdAt()).isEqualTo(existing.getCreatedAt());
    }

    @Test
    void findOrCreateByKeycloakId_createsWhenMissing() {
        when(userRepository.findByKeycloakId("kc-2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO dto = userService.findOrCreateByKeycloakId("kc-2");

        assertThat(dto.keycloakId()).isEqualTo("kc-2");
        assertThat(dto.createdAt()).isNotNull();
    }
}

