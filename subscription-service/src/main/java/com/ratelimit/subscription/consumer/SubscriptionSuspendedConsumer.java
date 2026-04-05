package com.ratelimit.subscription.consumer;

import com.ratelimit.subscription.dto.SubscriptionSuspendedEvent;
import com.ratelimit.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionSuspendedConsumer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionSuspendedConsumer.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionSuspendedConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "subscription-suspended", groupId = "subscription-service")
    public void handleSubscriptionSuspended(SubscriptionSuspendedEvent event) {
        log.info("Received subscription-suspended event for userId={}", event.userId());
        subscriptionService.suspendSubscription(event.userId());
    }
}
