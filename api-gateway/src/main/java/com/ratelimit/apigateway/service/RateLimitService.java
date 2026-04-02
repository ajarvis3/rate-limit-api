package com.ratelimit.apigateway.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if requests made to endpoints starting with /api exceed a threshold
     * @param exchange the ServerWebExchange
     * @return true if rate limit exceeded, false otherwise
     */
    public boolean checkRateLimit(ServerWebExchange exchange) {
        // Implement rate limiting logic using Redis
        // For example, use a key based on client IP and endpoint, and increment a counter with expiration
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String key = "rate_limit:" + userId;
        long systemTime = System.currentTimeMillis();
        long startTime = systemTime - 60000;

        redisTemplate.opsForZSet().add(key, Long.toString(systemTime), systemTime);

        redisTemplate.opsForZSet().remove(key, 0, startTime);

        Long count = redisTemplate.opsForZSet().count(key, 0, systemTime);

        // Temporary threshold
        // TODO: Update once "subscriptions" are fleshed out
        int threshold = 10;
        return count > threshold;
    }
}
