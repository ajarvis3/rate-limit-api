package com.ratelimit.usage.service;

import com.ratelimit.usage.model.ApiUsage;
import com.ratelimit.usage.repository.UsageRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.time.Instant;

@Service
public class UsageFlushService {


    private final StringRedisTemplate redisTemplate;
    private final UsageRepository usageEventRepository;

    public UsageFlushService(StringRedisTemplate redisTemplate, UsageRepository usageEventRepository) {
        this.redisTemplate = redisTemplate;
        this.usageEventRepository = usageEventRepository;
    }

    @Scheduled(fixedDelay = 60000) // every minute
    public void flush() {
        Set<String> keys = redisTemplate.keys("usage-counter:*");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String userId = key.replace("usage-counter:", "");
            String count = redisTemplate.opsForValue().getAndDelete(key);

            if (count == null) continue;

            ApiUsage event = new ApiUsage();
            event.setUserId(userId);
            event.setRequestCount(Long.parseLong(count));
            event.setTimestamp(Instant.now());

            usageEventRepository.save(event);
        }
    }

}
