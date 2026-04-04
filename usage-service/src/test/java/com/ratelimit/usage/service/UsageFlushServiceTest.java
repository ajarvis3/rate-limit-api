package com.ratelimit.usage.service;

import com.ratelimit.usage.model.ApiUsage;
import com.ratelimit.usage.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageFlushServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UsageRepository usageRepository;

    @Mock
    @SuppressWarnings("rawtypes")
    private ValueOperations valueOperations;

    private UsageFlushService usageFlushService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        usageFlushService = new UsageFlushService(redisTemplate, usageRepository);
    }

    @Test
    void flush_doesNothing_whenNoKeys() {
        when(redisTemplate.keys("usage-counter:*")).thenReturn(Set.of());

        usageFlushService.flush();

        verifyNoInteractions(usageRepository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void flush_savesApiUsageForEachKey() {
        when(redisTemplate.keys("usage-counter:*")).thenReturn(Set.of("usage-counter:user-1"));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndDelete("usage-counter:user-1")).thenReturn("15");

        usageFlushService.flush();

        ArgumentCaptor<ApiUsage> captor = ArgumentCaptor.forClass(ApiUsage.class);
        verify(usageRepository).save(captor.capture());
        ApiUsage saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getRequestCount()).isEqualTo(15L);
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void flush_skipsKey_whenCountIsNull() {
        when(redisTemplate.keys("usage-counter:*")).thenReturn(Set.of("usage-counter:user-2"));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndDelete("usage-counter:user-2")).thenReturn(null);

        usageFlushService.flush();

        verify(usageRepository, never()).save(any());
    }
}
