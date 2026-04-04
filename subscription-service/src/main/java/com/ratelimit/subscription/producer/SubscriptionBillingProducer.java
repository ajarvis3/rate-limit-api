package com.ratelimit.subscription.producer;

import com.ratelimit.subscription.config.KafkaTopicConfig;
import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionBillingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SubscriptionBillingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSubscriptionBilling(SubscriptionBillingDTO dto) {
        kafkaTemplate.send(KafkaTopicConfig.SUBCRIPTION_BILLING_TOPIC, dto.userId(), dto);
    }
}

