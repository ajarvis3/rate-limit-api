package com.ratelimit.billing.consumer;

import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import com.ratelimit.billing.service.InvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingKafkaConsumer {

    static final String USAGE_AGGREGATE_BILLING_TOPIC = "usage-aggregate-billing";

    private final InvoiceService invoiceService;

    public BillingKafkaConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = USAGE_AGGREGATE_BILLING_TOPIC, groupId = "billing-service")
    public void handleUsageAggregate(UsageAggregateBillingMessage message) {
        invoiceService.createInvoice(
                message.userId(),
                message.requestCount(),
                message.lastUpdated().toEpochMilli());
    }
}
