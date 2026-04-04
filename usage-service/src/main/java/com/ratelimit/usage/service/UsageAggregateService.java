package com.ratelimit.usage.service;

import com.ratelimit.usage.producer.BillingProducer;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import com.ratelimit.usage.repository.UsageAggregateRepository;
import com.ratelimit.usage.repository.UsageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsageAggregateService {

    UsageRepository usageRepository;
    UsageAggregateRepository usageAggregateRepository;
    BillingProducer billingProducer;

    public UsageAggregateService(UsageAggregateRepository usageAggregateRepository, UsageRepository usageRepository, BillingProducer billingProducer) {
        this.usageAggregateRepository = usageAggregateRepository;
        this.usageRepository = usageRepository;
        this.billingProducer = billingProducer;
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

                if (now.isAfter(aggregate.getTimestampEnd())) {
                    billingProducer.sendBillingMessage(new UsageAggregateBillingMessage(
                            aggregate.getUserId(),
                            aggregate.getRequestCount(),
                            aggregate.getLastUpdated(),
                            aggregate.getTimestampStart(),
                            aggregate.getTimestampEnd()));
                }
            });
    }
}
