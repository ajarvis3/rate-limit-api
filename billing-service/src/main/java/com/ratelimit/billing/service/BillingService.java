package com.ratelimit.billing.service;

import com.ratelimit.billing.dto.PaymentFailedEvent;
import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.ratelimit.subscription.dto.SubscriptionRenewedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
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
	 * Derives a deterministic UUID from a Long invoice ID using the ID as the
	 * least-significant bits (most-significant bits set to 0). This ensures
	 * a consistent, predictable UUID for each invoice without requiring a UUID
	 * column in the Invoice table.
	 */
	private static UUID toUuid(Long id) {
		return id != null ? new UUID(0L, id) : new UUID(0L, 0L);
	}

	/**
	 * Process an invoice: 50% chance to mark PAYMENT_FAILED (and emit a FailedBillingEvent),
	 * 50% to mark PAYMENT_COMPLETE. The invoice is persisted after status update.
	 */
	public void processInvoice(Invoice invoice) {
		invoice.setAttemptCount(invoice.getAttemptCount() + 1);
		boolean failed = ThreadLocalRandom.current().nextBoolean();

		if (failed) {
			invoice.setStatus("PAYMENT_FAILED");
			invoiceRepository.save(invoice);

			// publish legacy billing-failed event for dunning service
			FailedBillingEvent legacyEvent = new FailedBillingEvent(
					invoice.getUserId(),
					invoice.getId(),
					BigDecimal.valueOf(invoice.getAmount()),
					System.currentTimeMillis()
			);
			kafkaTemplate.send("billing-failed", invoice.getUserId(), legacyEvent);

			// publish new payment-failed event (only when userId is a valid UUID)
			try {
				PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
						UUID.fromString(invoice.getUserId()),
						toUuid(invoice.getId()),
						invoice.getAttemptCount()
				);
				kafkaTemplate.send("payment-failed", invoice.getUserId(), paymentFailedEvent);
			} catch (IllegalArgumentException ignored) {
				// userId is not a UUID; skip new event
			}
		} else {
			invoice.setStatus("PAYMENT_COMPLETE");
			invoiceRepository.save(invoice);

			// publish subscription-renewed event (only when userId is a valid UUID)
			try {
				SubscriptionRenewedEvent renewedEvent = new SubscriptionRenewedEvent(
						UUID.fromString(invoice.getUserId()),
						toUuid(invoice.getId())
				);
				kafkaTemplate.send("subscription-renewed", invoice.getUserId(), renewedEvent);
			} catch (IllegalArgumentException ignored) {
				// userId is not a UUID; skip new event
			}
		}
	}
}
