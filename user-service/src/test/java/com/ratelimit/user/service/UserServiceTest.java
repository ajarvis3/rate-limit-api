package com.ratelimit.user.service;

import com.ratelimit.user.dto.UserDTO;
import com.ratelimit.user.model.User;
import com.ratelimit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        userService = new UserService(userRepository, kafkaTemplate);
    }

    @Test
    void findOrCreateByKeycloakId_returnsExistingUser() {
        User existing = new User("kc-1", Instant.parse("2020-01-01T00:00:00Z"));
        when(userRepository.findByKeycloakId("kc-1")).thenReturn(Optional.of(existing));

        UserDTO dto = userService.findOrCreateByKeycloakId("kc-1", "FREE");

        assertThat(dto.keycloakId()).isEqualTo("kc-1");
        assertThat(dto.createdAt()).isEqualTo(existing.getCreatedAt());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    void findOrCreateByKeycloakId_createsWhenMissing() {
        when(userRepository.findByKeycloakId("kc-2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            org.springframework.test.util.ReflectionTestUtils.setField(u, "id", UUID.randomUUID());
            return u;
        });

        UserDTO dto = userService.findOrCreateByKeycloakId("kc-2", "PRO");

        assertThat(dto.keycloakId()).isEqualTo("kc-2");
        assertThat(dto.createdAt()).isNotNull();
        assertThat(dto.id()).isNotNull();
    }

    @Test
    void findOrCreateByKeycloakId_publishesUserCreatedEvent_forNewUser() {
        when(userRepository.findByKeycloakId("kc-3")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            org.springframework.test.util.ReflectionTestUtils.setField(u, "id", UUID.randomUUID());
            return u;
        });

        userService.findOrCreateByKeycloakId("kc-3", "ENTERPRISE");

        verify(kafkaTemplate).send(
                Mockito.eq(UserService.USER_CREATED_TOPIC),
                anyString(),
                any());
    }
}

