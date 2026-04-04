package com.ratelimit.usage.service;

import com.ratelimit.usage.producer.BillingProducer;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import com.ratelimit.usage.model.UsageAggregate;
import com.ratelimit.usage.repository.UsageAggregateRepository;
import com.ratelimit.usage.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsageAggregateServiceTest {

    private UsageAggregateRepository usageAggregateRepository;
    private UsageRepository usageRepository;
    private BillingProducer billingProducer;
    private UsageAggregateService usageAggregateService;

    @BeforeEach
    void setUp() {
        usageAggregateRepository = Mockito.mock(UsageAggregateRepository.class);
        usageRepository = Mockito.mock(UsageRepository.class);
        billingProducer = Mockito.mock(BillingProducer.class);
        usageAggregateService = new UsageAggregateService(usageAggregateRepository, usageRepository, billingProducer);
    }

    @Test
    void aggregateUsage_sendsKafkaMessage_whenPeriodHasEnded() {
        Instant pastEnd = Instant.now().minusSeconds(3600);
        UsageAggregate aggregate = new UsageAggregate(
                "user1", 10L,
                Instant.now().minusSeconds(7200),
                pastEnd,
                Instant.now().minusSeconds(7000));

        when(usageAggregateRepository.findActiveAggregates()).thenReturn(List.of(aggregate));
        when(usageRepository.sumByUserIdAndTimestampSince(eq("user1"), any())).thenReturn(5L);

        usageAggregateService.aggregateUsage();

        ArgumentCaptor<UsageAggregateBillingMessage> captor =
                ArgumentCaptor.forClass(UsageAggregateBillingMessage.class);
        verify(billingProducer, times(1)).sendBillingMessage(captor.capture());

        UsageAggregateBillingMessage sent = captor.getValue();
        assertEquals("user1", sent.userId());
        assertEquals(15L, sent.requestCount());
        assertEquals(pastEnd, sent.periodEnd());
    }

    @Test
    void aggregateUsage_doesNotSendKafkaMessage_whenPeriodIsStillActive() {
        Instant futureEnd = Instant.now().plusSeconds(3600);
        UsageAggregate aggregate = new UsageAggregate(
                "user2", 5L,
                Instant.now().minusSeconds(3600),
                futureEnd,
                Instant.now().minusSeconds(1800));

        when(usageAggregateRepository.findActiveAggregates()).thenReturn(List.of(aggregate));
        when(usageRepository.sumByUserIdAndTimestampSince(eq("user2"), any())).thenReturn(3L);

        usageAggregateService.aggregateUsage();

        verify(billingProducer, never()).sendBillingMessage(any());
    }
}
