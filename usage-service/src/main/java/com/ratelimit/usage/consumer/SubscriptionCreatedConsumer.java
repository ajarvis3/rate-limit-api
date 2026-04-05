package com.ratelimit.usage.consumer;

import com.ratelimit.subscription.dto.SubscriptionCreatedEvent;
import com.ratelimit.usage.service.UsageAggregateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionCreatedConsumer.class);

    private final UsageAggregateService usageAggregateService;

    public SubscriptionCreatedConsumer(UsageAggregateService usageAggregateService) {
        this.usageAggregateService = usageAggregateService;
    }

    @KafkaListener(topics = "subscription-created", groupId = "usage-service")
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Received subscription-created event for userId={}", event.userId());
        usageAggregateService.createAggregate(event.userId(), event.periodStart(), event.periodEnd());
    }
}
