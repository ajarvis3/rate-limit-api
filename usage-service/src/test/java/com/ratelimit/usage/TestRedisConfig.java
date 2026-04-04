package com.ratelimit.usage;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.Set;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = Mockito.mock(StringRedisTemplate.class);
        ValueOperations valueOps = Mockito.mock(ValueOperations.class);

        // Default behaviour: no keys, getAndDelete returns null
        Mockito.when(template.opsForValue()).thenReturn(valueOps);
        Mockito.when(valueOps.getAndDelete(Mockito.anyString())).thenReturn(null);
        Mockito.when(template.keys(Mockito.anyString())).thenReturn(Collections.emptySet());

        return template;
    }
}

