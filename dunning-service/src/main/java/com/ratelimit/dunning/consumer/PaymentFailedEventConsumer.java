package com.ratelimit.dunning.consumer;

import com.ratelimit.billing.dto.PaymentFailedEvent;
import com.ratelimit.dunning.service.DunningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentFailedEventConsumer.class);

    private final DunningService dunningService;

    public PaymentFailedEventConsumer(DunningService dunningService) {
        this.dunningService = dunningService;
    }

    @KafkaListener(topics = "payment-failed", groupId = "dunning-service")
    public void consume(PaymentFailedEvent event) {
        log.info("Received payment-failed event for userId={} attemptCount={}", event.userId(), event.attemptCount());
        dunningService.handlePaymentFailed(event);
    }
}
