package com.ratelimit.billing.consumer;

import com.ratelimit.billing.model.BillingPeriod;
import com.ratelimit.billing.repository.BillingPeriodRepository;
import com.ratelimit.subscription.dto.SubscriptionCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionCreatedConsumer.class);

    private final BillingPeriodRepository billingPeriodRepository;

    public SubscriptionCreatedConsumer(BillingPeriodRepository billingPeriodRepository) {
        this.billingPeriodRepository = billingPeriodRepository;
    }

    @KafkaListener(topics = "subscription-created", groupId = "billing-service")
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Received subscription-created event for userId={}", event.userId());
        BillingPeriod period = new BillingPeriod(event.userId(), event.periodStart(), event.periodEnd());
        billingPeriodRepository.save(period);
    }
}
