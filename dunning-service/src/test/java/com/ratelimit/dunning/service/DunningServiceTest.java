package com.ratelimit.dunning.service;

import com.ratelimit.dunning.dto.FailedBillingEvent;
import com.ratelimit.dunning.model.DunningRecord;
import com.ratelimit.dunning.producer.BillingRetryProducer;
import com.ratelimit.dunning.repository.DunningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DunningServiceTest {

    @Mock
    private DunningRepository repository;

    @Mock
    private BillingRetryProducer producer;

    private DunningService service;

    @BeforeEach
    void setUp() {
        service = new DunningService(repository, producer);
    }

    @Test
    void recordFailedBilling_persistsRecordWithOneHourRetryDelay() {
        long failedAt = 1_000_000L;
        FailedBillingEvent event = new FailedBillingEvent("user-1", 42L, 99.99, failedAt);

        service.recordFailedBilling(event);

        ArgumentCaptor<DunningRecord> captor = ArgumentCaptor.forClass(DunningRecord.class);
        verify(repository).save(captor.capture());
        DunningRecord saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getInvoiceId()).isEqualTo(42L);
        assertThat(saved.getAmount()).isEqualTo(99.99);
        assertThat(saved.getFailedAt()).isEqualTo(failedAt);
        assertThat(saved.getRetryAfter()).isEqualTo(failedAt + DunningService.RETRY_DELAY_MS);
        assertThat(saved.isRetried()).isFalse();
    }

    @Test
    void processRetries_sendsRetryAndMarksRetriedForDueRecords() {
        DunningRecord due = new DunningRecord("user-2", 7L, 50.0, 0L, 0L);
        when(repository.findByRetriedFalseAndRetryAfterLessThanEqual(anyLong()))
                .thenReturn(List.of(due));

        service.processRetries();

        verify(producer).sendRetry(due);
        assertThat(due.isRetried()).isTrue();
        verify(repository).save(due);
    }

    @Test
    void processRetries_doesNothingWhenNoRecordsDue() {
        when(repository.findByRetriedFalseAndRetryAfterLessThanEqual(anyLong()))
                .thenReturn(List.of());

        service.processRetries();

        verifyNoInteractions(producer);
        verify(repository, never()).save(any());
    }
}
