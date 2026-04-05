package com.ratelimit.apigateway.client;

import com.ratelimit.user.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://user-service:8081").build();
    }

    public Mono<UserDTO> getOrCreateUser(String keycloakId, String planTier) {
        return webClient.get()
                .uri("/user/" + keycloakId)
                .header("X-User-Plan", planTier)
                .retrieve()
                .bodyToMono(UserDTO.class);
    }
}
