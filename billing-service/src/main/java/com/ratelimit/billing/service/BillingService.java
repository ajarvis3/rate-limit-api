package com.ratelimit.billing.service;

import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BillingService {

	private final InvoiceRepository invoiceRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public BillingService(InvoiceRepository invoiceRepository, KafkaTemplate<String, Object> kafkaTemplate) {
		this.invoiceRepository = invoiceRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	/**
	 * Process an invoice: 50% chance to mark PAYMENT_FAILED (and emit a FailedBillingEvent),
	 * 50% to mark PAYMENT_COMPLETE. The invoice is persisted after status update.
	 */
	public void processInvoice(Invoice invoice) {
		boolean failed = ThreadLocalRandom.current().nextBoolean();

		if (failed) {
			invoice.setStatus("PAYMENT_FAILED");
			invoiceRepository.save(invoice);

			// produce a failed billing event to the dunning service
			FailedBillingEvent event = new FailedBillingEvent(
					invoice.getUserId(),
					invoice.getId(),
					BigDecimal.valueOf(invoice.getAmount()),
					System.currentTimeMillis()
			);
			kafkaTemplate.send("billing-failed", invoice.getUserId(), event);
		} else {
			invoice.setStatus("PAYMENT_COMPLETE");
			invoiceRepository.save(invoice);
		}
	}
}
