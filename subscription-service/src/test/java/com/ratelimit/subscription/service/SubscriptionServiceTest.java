package com.ratelimit.subscription.service;

import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import com.ratelimit.subscription.dto.SubscriptionCreatedEvent;
import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.model.Subscription;
import com.ratelimit.subscription.producer.SubscriptionBillingProducer;
import com.ratelimit.subscription.producer.SubscriptionEventProducer;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionBillingProducer billingProducer;

    @Mock
    private SubscriptionEventProducer eventProducer;

    private SubscriptionService subscriptionService;

    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, billingProducer, eventProducer);
    }

    @Test
    void getSubscription_returnsDto_whenSubscriptionExists() {
        Subscription subscription = new Subscription(USER_ID.toString(), "PREMIUM");
        when(subscriptionRepository.getSubscriptionByUserId(USER_ID)).thenReturn(subscription);

        SubscriptionDTO dto = subscriptionService.getSubscription(USER_ID.toString());

        assertThat(dto.userId()).isEqualTo(USER_ID.toString());
        assertThat(dto.planId()).isEqualTo("PREMIUM");
    }

    @Test
    void getSubscription_throwsNotFoundException_whenSubscriptionMissing() {
        UUID unknownId = UUID.randomUUID();
        when(subscriptionRepository.getSubscriptionByUserId(unknownId)).thenReturn(null);

        assertThatThrownBy(() -> subscriptionService.getSubscription(unknownId.toString()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getSubscription_withBillingMessage_returnsDto_andSendsToBillingProducer() {
        Instant now = Instant.now();
        UsageAggregateBillingMessage message = new UsageAggregateBillingMessage(
                USER_ID.toString(), 100L, now,
                now.minusSeconds(3600), now);
        Subscription subscription = new Subscription(USER_ID.toString(), "PREMIUM");
        when(subscriptionRepository.getSubscriptionByUserId(USER_ID)).thenReturn(subscription);

        SubscriptionBillingDTO result = subscriptionService.getSubscription(message);

        assertThat(result.userId()).isEqualTo(USER_ID.toString());
        assertThat(result.requestCount()).isEqualTo(100L);
        assertThat(result.subscription()).isEqualTo("PREMIUM");

        ArgumentCaptor<SubscriptionBillingDTO> captor = ArgumentCaptor.forClass(SubscriptionBillingDTO.class);
        verify(billingProducer, times(1)).sendSubscriptionBilling(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(USER_ID.toString());
    }

    @Test
    void getSubscription_withBillingMessage_usesFreeLevel_whenSubscriptionNotFound() {
        UUID otherId = UUID.randomUUID();
        Instant now = Instant.now();
        UsageAggregateBillingMessage message = new UsageAggregateBillingMessage(
                otherId.toString(), 10L, now,
                now.minusSeconds(3600), now);
        when(subscriptionRepository.getSubscriptionByUserId(otherId)).thenReturn(null);

        SubscriptionBillingDTO result = subscriptionService.getSubscription(message);

        assertThat(result.subscription()).isEqualTo("FREE");
        verify(billingProducer, times(1)).sendSubscriptionBilling(any());
    }

    @Test
    void createSubscription_savesAndPublishesEvent() {
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        subscriptionService.createSubscription(USER_ID, "PRO");

        verify(subscriptionRepository).save(any(Subscription.class));
        ArgumentCaptor<SubscriptionCreatedEvent> captor = ArgumentCaptor.forClass(SubscriptionCreatedEvent.class);
        verify(eventProducer).sendSubscriptionCreated(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(USER_ID);
    }

    @Test
    void renewSubscription_rollsForwardPeriodAndPublishesEvent() {
        Instant start = Instant.now().minusSeconds(30L * 24 * 3600);
        Instant end = Instant.now();
        Subscription subscription = new Subscription(USER_ID, "PRO", "ACTIVE", start, end);
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        subscriptionService.renewSubscription(USER_ID, UUID.randomUUID());

        assertThat(subscription.getCurrentPeriodStart()).isEqualTo(end);
        verify(eventProducer).sendSubscriptionCreated(any(SubscriptionCreatedEvent.class));
    }

    @Test
    void suspendSubscription_setsStatusToSuspended() {
        Subscription subscription = new Subscription(USER_ID, "PRO", "ACTIVE",
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        subscriptionService.suspendSubscription(USER_ID);

        assertThat(subscription.getStatus()).isEqualTo("SUSPENDED");
        verify(subscriptionRepository).save(subscription);
    }
}
