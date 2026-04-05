package com.ratelimit.dunning.producer;

import com.ratelimit.subscription.dto.SubscriptionSuspendedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionSuspendedProducer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionSuspendedProducer.class);

    static final String TOPIC = "subscription-suspended";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SubscriptionSuspendedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSuspended(SubscriptionSuspendedEvent event) {
        log.info("Sending subscription-suspended event for userId={}", event.userId());
        kafkaTemplate.send(TOPIC, event.userId().toString(), event);
    }
}
