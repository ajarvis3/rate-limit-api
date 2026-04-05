package com.ratelimit.billing.service;

import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(invoiceRepository, kafkaTemplate);
    }

    @Test
    void processInvoice_whenFailed_setsStatusAndSendsFailedBillingEvent() {
        try (MockedStatic<ThreadLocalRandom> mockedRandom = mockStatic(ThreadLocalRandom.class)) {
            ThreadLocalRandom randomMock = mock(ThreadLocalRandom.class);
            mockedRandom.when(ThreadLocalRandom::current).thenReturn(randomMock);
            when(randomMock.nextBoolean()).thenReturn(true);

            String userId = UUID.randomUUID().toString();
            Invoice invoice = new Invoice(userId, 1_000_000L, 49.99, "FREE");
            billingService.processInvoice(invoice);

            assertThat(invoice.getStatus()).isEqualTo("PAYMENT_FAILED");
            assertThat(invoice.getAttemptCount()).isEqualTo(1);
            verify(invoiceRepository).save(invoice);
            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(kafkaTemplate).send(eq("billing-failed"), eq(userId), eventCaptor.capture());
            FailedBillingEvent sentEvent = (FailedBillingEvent) eventCaptor.getValue();
            assertThat(sentEvent.userId()).isEqualTo(userId);
            assertThat(sentEvent.amount()).isEqualByComparingTo("49.99");
            // also verify payment-failed event was sent with attempt count
            verify(kafkaTemplate).send(eq("payment-failed"), eq(userId), any());
        }
    }

    @Test
    void processInvoice_whenSuccessful_setsStatusCompleteAndSendsRenewedEvent() {
        try (MockedStatic<ThreadLocalRandom> mockedRandom = mockStatic(ThreadLocalRandom.class)) {
            ThreadLocalRandom randomMock = mock(ThreadLocalRandom.class);
            mockedRandom.when(ThreadLocalRandom::current).thenReturn(randomMock);
            when(randomMock.nextBoolean()).thenReturn(false);

            String userId = UUID.randomUUID().toString();
            Invoice invoice = new Invoice(userId, 1_000_000L, 99.99, "PREMIUM");
            billingService.processInvoice(invoice);

            assertThat(invoice.getStatus()).isEqualTo("PAYMENT_COMPLETE");
            verify(invoiceRepository).save(invoice);
            verify(kafkaTemplate).send(eq("subscription-renewed"), eq(userId), any());
        }
    }

    @Test
    void processInvoice_whenFailed_withNonUuidUserId_onlySendsLegacyEvent() {
        try (MockedStatic<ThreadLocalRandom> mockedRandom = mockStatic(ThreadLocalRandom.class)) {
            ThreadLocalRandom randomMock = mock(ThreadLocalRandom.class);
            mockedRandom.when(ThreadLocalRandom::current).thenReturn(randomMock);
            when(randomMock.nextBoolean()).thenReturn(true);

            Invoice invoice = new Invoice("user-1", 1_000_000L, 49.99, "FREE");
            billingService.processInvoice(invoice);

            assertThat(invoice.getStatus()).isEqualTo("PAYMENT_FAILED");
            verify(kafkaTemplate).send(eq("billing-failed"), eq("user-1"), any());
            // payment-failed should NOT be sent since userId is not a UUID
            verify(kafkaTemplate, never()).send(eq("payment-failed"), any(), any());
        }
    }
}
