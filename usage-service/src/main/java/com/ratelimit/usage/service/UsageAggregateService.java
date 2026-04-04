package com.ratelimit.usage.service;

import com.ratelimit.usage.repository.UsageAggregateRepository;
import com.ratelimit.usage.repository.UsageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsageAggregateService {

    UsageRepository usageRepository;
    UsageAggregateRepository usageAggregateRepository;

    public UsageAggregateService(UsageAggregateRepository usageAggregateRepository, UsageRepository usageRepository) {
        this.usageAggregateRepository = usageAggregateRepository;
        this.usageRepository = usageRepository;
    }

    @Scheduled(fixedDelay = 600_000) // every 10 minutes
    public void aggregateUsage() {
        Instant now = Instant.now();
        usageAggregateRepository.findActiveAggregates()
            .forEach(aggregate -> {
                Long sum = usageRepository.sumByUserIdAndTimestampSince(aggregate.getUserId(), aggregate.getLastUpdated());
                if (sum != null) {
                    aggregate.setRequestCount(aggregate.getRequestCount() + sum);
                    aggregate.setLastUpdated(now);
                }
                usageAggregateRepository.save(aggregate);
            });
    }
}
