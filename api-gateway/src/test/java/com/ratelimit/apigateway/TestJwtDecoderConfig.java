package com.ratelimit.apigateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Configuration
public class TestJwtDecoderConfig {

    @Bean
    @Primary
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> {
            Instant now = Instant.now();
            Map<String, Object> headers = Map.of("alg", "none");
            Map<String, Object> claims = Map.of("sub", "test-user", "scope", "openid");
            Jwt jwt = new Jwt(token, now, now.plusSeconds(3600), headers, claims);
            return Mono.just(jwt);
        };
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> {
            Instant now = Instant.now();
            Map<String, Object> headers = Map.of("alg", "none");
            Map<String, Object> claims = Map.of("sub", "test-user", "scope", "openid");
            return new Jwt(token, now, now.plusSeconds(3600), headers, claims);
        };
    }
}

