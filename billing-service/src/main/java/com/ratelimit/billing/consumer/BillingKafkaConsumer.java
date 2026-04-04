package com.ratelimit.billing.consumer;

import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import com.ratelimit.billing.service.InvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingKafkaConsumer {

    static final String SUBSCRIPTION_BILLING_TOPIC = "subscription-billing";

    private final InvoiceService invoiceService;

    public BillingKafkaConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = SUBSCRIPTION_BILLING_TOPIC, groupId = "billing-service")
    public void handleSubscriptionBilling(SubscriptionBillingDTO message) {
        invoiceService.createInvoice(
                message.userId(),
                message.requestCount(),
                message.lastUpdated().toEpochMilli(),
                message.subscription());
    }
}
