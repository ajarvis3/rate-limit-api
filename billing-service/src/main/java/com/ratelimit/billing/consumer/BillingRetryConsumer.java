package com.ratelimit.billing.consumer;

import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.billing.service.BillingService;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class BillingRetryConsumer {

    private static final Logger log = Logger.getLogger(BillingRetryConsumer.class.getName());

    private final InvoiceRepository invoiceRepository;
    private final BillingService billingService;

    public BillingRetryConsumer(InvoiceRepository invoiceRepository, BillingService billingService) {
        this.invoiceRepository = invoiceRepository;
        this.billingService = billingService;
    }

    @KafkaListener(topics = "billing-retry", groupId = "billing-service")
    public void consume(FailedBillingEvent event) {
        if (event == null) {
            log.warning("Received null FailedBillingEvent on billing-retry topic");
            return;
        }

        Long invoiceId = event.invoiceId();
        invoiceRepository.findById(invoiceId).ifPresentOrElse(
                (Invoice invoice) -> {
                    log.info(() -> "Received billing retry for invoice=" + invoice.getId() + " user=" + invoice.getUserId());
                    billingService.processInvoice(invoice);
                },
                () -> log.warning("Invoice not found for retry invoiceId=" + invoiceId)
        );
    }
}

