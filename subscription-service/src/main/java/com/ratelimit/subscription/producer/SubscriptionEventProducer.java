package com.ratelimit.subscription.producer;

import com.ratelimit.subscription.dto.SubscriptionCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionEventProducer {

    static final String SUBSCRIPTION_CREATED_TOPIC = "subscription-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SubscriptionEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSubscriptionCreated(SubscriptionCreatedEvent event) {
        kafkaTemplate.send(SUBSCRIPTION_CREATED_TOPIC, event.userId().toString(), event);
    }
}
