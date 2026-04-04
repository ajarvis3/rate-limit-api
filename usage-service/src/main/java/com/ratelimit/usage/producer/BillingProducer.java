package com.ratelimit.usage.producer;

import com.ratelimit.usage.config.KafkaTopicConfig;import com.ratelimit.usage.dto.UsageAggregateBillingMessage;import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BillingProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public BillingProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBillingMessage(UsageAggregateBillingMessage message) {
        kafkaTemplate.send(KafkaTopicConfig.USAGE_AGGREGATE_BILLING_TOPIC, message.userId(), message);
    }
}
