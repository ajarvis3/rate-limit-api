package com.ratelimit.usage.service;

import com.ratelimit.usage.dto.UsageResponseDTO;
import com.ratelimit.usage.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UsageServiceTest {

    private UsageRepository usageRepository;
    private StringRedisTemplate redisTemplate;
    private UsageService usageService;

    @BeforeEach
    void setUp() {
        usageRepository = Mockito.mock(UsageRepository.class);
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        usageService = new UsageService(usageRepository, redisTemplate);
    }

    @Test
    void getUsage_returnsCountFromRepository() {
        when(usageRepository.countByUserIdAndTimestampBetween(eq("user-1"), eq(Instant.EPOCH), any(Instant.class)))
                .thenReturn(75L);

        UsageResponseDTO result = usageService.getUsage("user-1");

        assertThat(result.numberOfRequests()).isEqualTo(75L);
    }

    @Test
    void getUsage_returnsZero_whenNoRequests() {
        when(usageRepository.countByUserIdAndTimestampBetween(eq("user-2"), eq(Instant.EPOCH), any(Instant.class)))
                .thenReturn(0L);

        UsageResponseDTO result = usageService.getUsage("user-2");

        assertThat(result.numberOfRequests()).isEqualTo(0L);
    }
}
