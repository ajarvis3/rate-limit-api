package com.ratelimit.user.service;

import com.ratelimit.user.dto.UserDTO;
import com.ratelimit.user.model.User;
import com.ratelimit.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO findOrCreateByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> userRepository.save(new User(keycloakId, Instant.now())));
        return new UserDTO(user.getKeycloakId(), user.getCreatedAt());
    }
}

