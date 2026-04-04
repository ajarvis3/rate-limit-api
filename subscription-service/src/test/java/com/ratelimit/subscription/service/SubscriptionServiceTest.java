package com.ratelimit.subscription.service;

import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.model.Subscription;
import com.ratelimit.subscription.producer.SubscriptionBillingProducer;
import com.ratelimit.subscription.repository.SubscriptionRepository;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionBillingProducer producer;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, producer);
    }

    @Test
    void getSubscription_returnsDto_whenSubscriptionExists() {
        Subscription subscription = new Subscription("user-1", "PREMIUM");
        when(subscriptionRepository.getSubscriptionByUserId("user-1")).thenReturn(subscription);

        SubscriptionDTO dto = subscriptionService.getSubscription("user-1");

        assertThat(dto.userId()).isEqualTo("user-1");
        assertThat(dto.planId()).isEqualTo("PREMIUM");
    }

    @Test
    void getSubscription_throwsNotFoundException_whenSubscriptionMissing() {
        when(subscriptionRepository.getSubscriptionByUserId("user-2")).thenReturn(null);

        assertThatThrownBy(() -> subscriptionService.getSubscription("user-2"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getSubscription_withBillingMessage_returnsDto_andSendsToBillingProducer() {
        Instant now = Instant.now();
        UsageAggregateBillingMessage message = new UsageAggregateBillingMessage(
                "user-1", 100L, now,
                now.minusSeconds(3600), now);
        Subscription subscription = new Subscription("user-1", "PREMIUM");
        when(subscriptionRepository.getSubscriptionByUserId("user-1")).thenReturn(subscription);

        SubscriptionBillingDTO result = subscriptionService.getSubscription(message);

        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.requestCount()).isEqualTo(100L);
        assertThat(result.subscription()).isEqualTo("PREMIUM");

        ArgumentCaptor<SubscriptionBillingDTO> captor = ArgumentCaptor.forClass(SubscriptionBillingDTO.class);
        verify(producer, times(1)).sendSubscriptionBilling(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo("user-1");
    }

    @Test
    void getSubscription_withBillingMessage_usesFreeLevel_whenSubscriptionNotFound() {
        Instant now = Instant.now();
        UsageAggregateBillingMessage message = new UsageAggregateBillingMessage(
                "user-3", 10L, now,
                now.minusSeconds(3600), now);
        when(subscriptionRepository.getSubscriptionByUserId("user-3")).thenReturn(null);

        SubscriptionBillingDTO result = subscriptionService.getSubscription(message);

        assertThat(result.subscription()).isEqualTo("FREE");
        verify(producer, times(1)).sendSubscriptionBilling(any());
    }
}
