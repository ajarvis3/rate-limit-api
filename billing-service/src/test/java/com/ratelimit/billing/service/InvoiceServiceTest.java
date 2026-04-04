package com.ratelimit.billing.service;

import com.ratelimit.billing.dto.InvoiceResponseDTO;
import com.ratelimit.billing.exception.InvoiceNotFoundException;
import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private BillingService billingService;

    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        invoiceService = new InvoiceService(invoiceRepository, billingService);
    }

    @Test
    void createInvoice_savesInvoiceAndProcesses() {
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        invoiceService.createInvoice("user-1", 50L, 1_000_000L, "PREMIUM");

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());
        Invoice saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getAmount()).isEqualTo(50.0);
        assertThat(saved.getBilledAt()).isEqualTo(1_000_000L);
        assertThat(saved.getSubscription()).isEqualTo("PREMIUM");

        verify(billingService).processInvoice(any(Invoice.class));
    }

    @Test
    void getById_returnsDto_whenFound() {
        Invoice invoice = new Invoice("user-1", 1_000_000L, 99.99, "PREMIUM");
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        InvoiceResponseDTO dto = invoiceService.getById(1L);

        assertThat(dto.status()).isEqualTo("BILLED");
        assertThat(dto.totalAmount()).isEqualTo(99.99);
    }

    @Test
    void getById_throwsException_whenNotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getById(99L))
                .isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    void getMostRecentByUserId_returnsDto_whenFound() {
        Invoice invoice = new Invoice("user-1", 2_000_000L, 19.99, "FREE");
        when(invoiceRepository.findTopByUserIdOrderByBilledAtDesc("user-1")).thenReturn(Optional.of(invoice));

        InvoiceResponseDTO dto = invoiceService.getMostRecentByUserId("user-1");

        assertThat(dto.totalAmount()).isEqualTo(19.99);
        assertThat(dto.status()).isEqualTo("BILLED");
    }

    @Test
    void getMostRecentByUserId_throwsException_whenNotFound() {
        when(invoiceRepository.findTopByUserIdOrderByBilledAtDesc("user-2")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getMostRecentByUserId("user-2"))
                .isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    void getInvoicesForUserBetween_returnsList_whenFound() {
        Invoice invoice = new Invoice("user-1", 1_500_000L, 9.99, "FREE");
        when(invoiceRepository.findByUserIdAndBilledAtBetween("user-1", 0L, 2_000_000L))
                .thenReturn(List.of(invoice));

        List<InvoiceResponseDTO> result = invoiceService.getInvoicesForUserBetween("user-1", 0L, 2_000_000L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).totalAmount()).isEqualTo(9.99);
    }

    @Test
    void getInvoicesForUserBetween_throwsException_whenEmpty() {
        when(invoiceRepository.findByUserIdAndBilledAtBetween("user-1", 0L, 1000L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> invoiceService.getInvoicesForUserBetween("user-1", 0L, 1000L))
                .isInstanceOf(InvoiceNotFoundException.class);
    }
}
