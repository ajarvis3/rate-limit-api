package com.ratelimit.billing.kafka;

import com.ratelimit.billing.service.InvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingKafkaConsumer {

    private final InvoiceService invoiceService;

    public BillingKafkaConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = "usage-aggregate-billing", groupId = "billing-service")
    public void handleUsageAggregate(UsageAggregateBillingMessage message) {
        invoiceService.createInvoice(
                message.userId(),
                message.requestCount(),
                message.lastUpdated().toEpochMilli());
    }
}
