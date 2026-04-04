package com.ratelimit.billing.kafka;

import com.ratelimit.billing.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BillingRetryConsumerTest {

    private InvoiceService invoiceService;
    private BillingRetryConsumer billingRetryConsumer;

    @BeforeEach
    void setUp() {
        invoiceService = Mockito.mock(InvoiceService.class);
        billingRetryConsumer = new BillingRetryConsumer(invoiceService);
    }

    @Test
    void consume_retriesBilling_fromEvent() {
        FailedBillingEvent event = new FailedBillingEvent("user1", 42L, new BigDecimal("100"), 1_000_000L);

        billingRetryConsumer.consume(event);

        verify(invoiceService, times(1)).retryBilling(42L, 100.0);
    }
}
