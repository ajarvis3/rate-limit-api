package com.ratelimit.billing.consumer;

import com.ratelimit.billing.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BillingKafkaConsumerTest {

    private InvoiceService invoiceService;
    private BillingKafkaConsumer billingKafkaConsumer;

    @BeforeEach
    void setUp() {
        invoiceService = Mockito.mock(InvoiceService.class);
        billingKafkaConsumer = new BillingKafkaConsumer(invoiceService);
    }

    @Test
    void handleUsageAggregate_createsInvoice_fromMessage() {
        Instant lastUpdated = Instant.ofEpochMilli(1_000_000L);
        com.ratelimit.billing.dto.UsageAggregateBillingMessage message = new com.ratelimit.billing.dto.UsageAggregateBillingMessage(
                "user1",
                42L,
                lastUpdated,
                Instant.ofEpochMilli(0L),
                Instant.ofEpochMilli(2_000_000L));

        billingKafkaConsumer.handleUsageAggregate(message);

        verify(invoiceService, times(1)).createInvoice("user1", 42L, lastUpdated.toEpochMilli());
    }
}
