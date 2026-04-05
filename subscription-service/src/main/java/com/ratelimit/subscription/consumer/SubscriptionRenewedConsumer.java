package com.ratelimit.subscription.consumer;

import com.ratelimit.subscription.dto.SubscriptionRenewedEvent;
import com.ratelimit.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionRenewedConsumer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionRenewedConsumer.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionRenewedConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "subscription-renewed", groupId = "subscription-service")
    public void handleSubscriptionRenewed(SubscriptionRenewedEvent event) {
        log.info("Received subscription-renewed event for userId={}", event.userId());
        subscriptionService.renewSubscription(event.userId(), event.subscriptionId());
    }
}
