package com.ratelimit.usage.service;

import com.ratelimit.usage.dto.UsageResponseDTO;
import com.ratelimit.usage.repository.UsageRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Instant;

public class UsageService {

    UsageRepository usageRepository;

    private final StringRedisTemplate redisTemplate;

    public UsageService(UsageRepository usageRepository, StringRedisTemplate redisTemplate) {
        this.usageRepository = usageRepository;
        this.redisTemplate = redisTemplate;
    }

    public UsageResponseDTO getUsage(String userId) {
        Instant start = Instant.EPOCH;
        Instant end = Instant.now(); // temporary until a database is up
        long numberOfRequests = usageRepository.countByUserIdAndTimestampBetween(userId, start, end);
        return new UsageResponseDTO(numberOfRequests);
    }
}
