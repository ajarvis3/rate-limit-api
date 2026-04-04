package com.ratelimit.dunning.service;

import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.ratelimit.dunning.model.DunningRecord;
import com.ratelimit.dunning.producer.BillingRetryProducer;
import com.ratelimit.dunning.repository.DunningRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DunningService {

    static final long RETRY_DELAY_MS = 3_600_000L; // 1 hour

    private final DunningRepository repository;
    private final BillingRetryProducer producer;

    public DunningService(DunningRepository repository, BillingRetryProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public void recordFailedBilling(FailedBillingEvent event) {
        DunningRecord record = new DunningRecord(
                event.userId(),
                event.invoiceId(),
                event.amount(),
                event.failedAt(),
                event.failedAt() + RETRY_DELAY_MS
        );
        repository.save(record);
    }

    @Scheduled(fixedDelay = 60_000)
    public void processRetries() {
        long now = System.currentTimeMillis();
        List<DunningRecord> due = repository.findByRetriedFalseAndRetryAfterLessThanEqual(now);
        for (DunningRecord record : due) {
            producer.sendRetry(record);
            record.setRetried(true);
            repository.save(record);
        }
    }
}
