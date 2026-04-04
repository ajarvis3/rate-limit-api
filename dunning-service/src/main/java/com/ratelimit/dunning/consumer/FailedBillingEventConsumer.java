package com.ratelimit.dunning.consumer;

import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.ratelimit.dunning.service.DunningService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FailedBillingEventConsumer {

    private final DunningService dunningService;

    public FailedBillingEventConsumer(DunningService dunningService) {
        this.dunningService = dunningService;
    }

    @KafkaListener(topics = "billing-failed", groupId = "dunning-service")
    public void consume(FailedBillingEvent event) {
        dunningService.recordFailedBilling(event);
    }
}
