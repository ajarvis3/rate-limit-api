package com.ratelimit.dunning.producer;

import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.ratelimit.dunning.model.DunningRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BillingRetryProducer {

    static final String TOPIC = "billing-retry";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BillingRetryProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRetry(DunningRecord record) {
        FailedBillingEvent event = new FailedBillingEvent(
                record.getUserId(),
                record.getInvoiceId(),
                record.getAmount(),
                record.getFailedAt()
        );
        kafkaTemplate.send(TOPIC, record.getUserId(), event);
    }
}
