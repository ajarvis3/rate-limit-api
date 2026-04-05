package com.ratelimit.subscription.consumer;

import com.ratelimit.subscription.service.SubscriptionService;
import com.ratelimit.user.dto.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedEventConsumer.class);

    private final SubscriptionService subscriptionService;

    public UserCreatedEventConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "user-created", groupId = "subscription-service")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user-created event for userId={} planName={}", event.userId(), event.planName());
        subscriptionService.createSubscription(event.userId(), event.planName());
    }
}
