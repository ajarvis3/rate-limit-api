package com.ratelimit.billing.kafka;

import com.ratelimit.billing.service.InvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingRetryConsumer {

    static final String BILLING_RETRY_TOPIC = "billing.retry";

    private final InvoiceService invoiceService;

    public BillingRetryConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = BILLING_RETRY_TOPIC, groupId = "billing-service",
            containerFactory = "billingRetryContainerFactory")
    public void consume(FailedBillingEvent event) {
        invoiceService.retryBilling(event.invoiceId(), event.amount().doubleValue());
    }
}
