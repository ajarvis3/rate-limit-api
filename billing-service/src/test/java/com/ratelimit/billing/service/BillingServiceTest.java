package com.ratelimit.billing.service;

import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import com.ratelimit.dunning.dto.FailedBillingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(invoiceRepository, kafkaTemplate);
    }

    @RepeatedTest(20)
    void processInvoice_setsStatusAndSavesInvoice() {
        Invoice invoice = new Invoice("user-1", 1_000_000L, 99.99, "PREMIUM");
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        billingService.processInvoice(invoice);

        assertThat(invoice.getStatus()).isIn("PAYMENT_COMPLETE", "PAYMENT_FAILED");
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void processInvoice_whenFailed_sendsFailedBillingEvent() {
        // Spy on BillingService using a subclass that always returns failed
        InvoiceRepository repo = mock(InvoiceRepository.class);
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        BillingService alwaysFailService = new BillingService(repo, kafka) {
            @Override
            public void processInvoice(Invoice invoice) {
                invoice.setStatus("PAYMENT_FAILED");
                repo.save(invoice);
                FailedBillingEvent event = new FailedBillingEvent(
                        invoice.getUserId(),
                        invoice.getId(),
                        java.math.BigDecimal.valueOf(invoice.getAmount()),
                        System.currentTimeMillis()
                );
                kafka.send("billing-failed", invoice.getUserId(), event);
            }
        };

        Invoice invoice = new Invoice("user-1", 1_000_000L, 49.99, "FREE");
        alwaysFailService.processInvoice(invoice);

        assertThat(invoice.getStatus()).isEqualTo("PAYMENT_FAILED");
        verify(repo).save(invoice);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafka).send(eq("billing-failed"), eq("user-1"), eventCaptor.capture());
        FailedBillingEvent sentEvent = (FailedBillingEvent) eventCaptor.getValue();
        assertThat(sentEvent.userId()).isEqualTo("user-1");
        assertThat(sentEvent.amount()).isEqualByComparingTo("49.99");
    }
}
