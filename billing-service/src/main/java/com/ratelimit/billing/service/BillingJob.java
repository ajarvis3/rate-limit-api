package com.ratelimit.billing.service;

import com.ratelimit.billing.model.BillingPeriod;
import com.ratelimit.billing.repository.BillingPeriodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BillingJob {

    private static final Logger log = LoggerFactory.getLogger(BillingJob.class);

    private final BillingPeriodRepository billingPeriodRepository;
    private final InvoiceService invoiceService;

    public BillingJob(BillingPeriodRepository billingPeriodRepository, InvoiceService invoiceService) {
        this.billingPeriodRepository = billingPeriodRepository;
        this.invoiceService = invoiceService;
    }

    /**
     * Midnight daily job: find billing periods that have ended and haven't been invoiced yet.
     * For each one, create an invoice and attempt payment.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processDueBillingPeriods() {
        log.info("BillingJob: processing due billing periods");
        List<BillingPeriod> duePeriods = billingPeriodRepository
                .findByInvoiceCreatedFalseAndPeriodEndBefore(Instant.now());

        for (BillingPeriod period : duePeriods) {
            try {
                invoiceService.createInvoice(
                        period.getUserId().toString(),
                        0L,
                        period.getPeriodEnd().toEpochMilli(),
                        "SUBSCRIPTION");
                period.setInvoiceCreated(true);
                billingPeriodRepository.save(period);
                log.info("BillingJob: created invoice for userId={}", period.getUserId());
            } catch (Exception e) {
                log.error("BillingJob: failed to create invoice for userId={}", period.getUserId(), e);
            }
        }
    }

    /**
     * Hourly safety net: find billing periods past their end date with no invoice created.
     * Retries invoice creation for any missed periods.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void safetyNetJob() {
        log.info("BillingJob safety net: checking for missed invoices");
        List<BillingPeriod> missed = billingPeriodRepository
                .findByInvoiceCreatedFalseAndPeriodEndBefore(Instant.now());

        for (BillingPeriod period : missed) {
            try {
                invoiceService.createInvoice(
                        period.getUserId().toString(),
                        0L,
                        period.getPeriodEnd().toEpochMilli(),
                        "SUBSCRIPTION");
                period.setInvoiceCreated(true);
                billingPeriodRepository.save(period);
                log.info("BillingJob safety net: created invoice for userId={}", period.getUserId());
            } catch (Exception e) {
                log.error("BillingJob safety net: failed for userId={}", period.getUserId(), e);
            }
        }
    }
}
