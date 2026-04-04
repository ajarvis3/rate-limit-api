package com.ratelimit.subscription.consumer;

import com.ratelimit.subscription.service.SubscriptionService;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionKafkaConsumerTest {

    @Mock
    private SubscriptionService subscriptionService;

    private SubscriptionKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new SubscriptionKafkaConsumer(subscriptionService);
    }

    @Test
    void handleUsageAggregate_delegatesToSubscriptionService() {
        Instant now = Instant.now();
        UsageAggregateBillingMessage message = new UsageAggregateBillingMessage(
                "user-1", 42L, now,
                now.minusSeconds(3600), now);

        consumer.handleUsageAggregate(message);

        verify(subscriptionService).getSubscription(message);
    }
}
