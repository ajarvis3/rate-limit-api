package com.ratelimit.user.service;

import com.ratelimit.user.dto.UserCreatedEvent;
import com.ratelimit.user.dto.UserDTO;
import com.ratelimit.user.model.User;
import com.ratelimit.user.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    static final String USER_CREATED_TOPIC = "user-created";

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UserDTO findOrCreateByKeycloakId(String keycloakId, String planName) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> new UserDTO(user.getId(), user.getKeycloakId(), user.getCreatedAt()))
                .orElseGet(() -> {
                    User saved = userRepository.save(new User(keycloakId, Instant.now()));
                    kafkaTemplate.send(USER_CREATED_TOPIC, saved.getId().toString(),
                            new UserCreatedEvent(saved.getId(), saved.getKeycloakId(), planName));
                    return new UserDTO(saved.getId(), saved.getKeycloakId(), saved.getCreatedAt());
                });
    }
}

