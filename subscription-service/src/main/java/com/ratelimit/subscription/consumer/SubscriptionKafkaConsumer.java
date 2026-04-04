package com.ratelimit.subscription.consumer;

import com.ratelimit.subscription.service.SubscriptionService;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SubscriptionKafkaConsumer {

    private final SubscriptionService subscriptionService;

    private static final Logger log = Logger.getLogger(SubscriptionKafkaConsumer.class.getName());
    static final String USAGE_AGGREGATE_BILLING_TOPIC = "usage-aggregate-billing";

    public SubscriptionKafkaConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = USAGE_AGGREGATE_BILLING_TOPIC, groupId = "subscription-service")
    public void handleUsageAggregate(UsageAggregateBillingMessage message) {
        // For now, log the incoming billing message. The billing service should be responsible for creating invoices.
        log.info(() -> "Received usage aggregate for billing: user=" + message.userId() + " count=" + message.requestCount());
        subscriptionService.getSubscription(message);
    }
}
