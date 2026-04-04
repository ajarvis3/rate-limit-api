package com.ratelimit.billing.consumer;

import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.billing.service.BillingService;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingRetryConsumerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private BillingService billingService;

    private BillingRetryConsumer billingRetryConsumer;

    @BeforeEach
    void setUp() {
        billingRetryConsumer = new BillingRetryConsumer(invoiceRepository, billingService);
    }

    @Test
    void consume_withNullEvent_doesNothing() {
        billingRetryConsumer.consume(null);

        verifyNoInteractions(invoiceRepository, billingService);
    }

    @Test
    void consume_retriesInvoice_whenFound() {
        FailedBillingEvent event = new FailedBillingEvent("user-1", 42L, new BigDecimal("99.99"), 1_000_000L);
        Invoice invoice = new Invoice("user-1", 1_000_000L, 99.99, "PREMIUM");
        when(invoiceRepository.findById(42L)).thenReturn(Optional.of(invoice));

        billingRetryConsumer.consume(event);

        verify(billingService).processInvoice(invoice);
    }

    @Test
    void consume_doesNothing_whenInvoiceNotFound() {
        FailedBillingEvent event = new FailedBillingEvent("user-1", 99L, new BigDecimal("9.99"), 1_000_000L);
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        billingRetryConsumer.consume(event);

        verifyNoInteractions(billingService);
    }
}
