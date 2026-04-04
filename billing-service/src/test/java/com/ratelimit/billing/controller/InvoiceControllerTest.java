package com.ratelimit.billing.controller;

import com.ratelimit.billing.dto.InvoiceResponseDTO;
import com.ratelimit.billing.exception.InvoiceNotFoundException;
import com.ratelimit.billing.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class InvoiceControllerTest {

    private InvoiceService invoiceService;
    private InvoiceController invoiceController;

    @BeforeEach
    void setUp() {
        invoiceService = Mockito.mock(InvoiceService.class);
        invoiceController = new InvoiceController(invoiceService);
    }

    @Test
    void getById_returnsOk_whenFound() {
        InvoiceResponseDTO dto = new InvoiceResponseDTO(1L, null, null, "BILLED", 99.99);
        when(invoiceService.getById(1L)).thenReturn(dto);

        ResponseEntity<InvoiceResponseDTO> response = invoiceController.getById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void getById_propagatesException_whenNotFound() {
        when(invoiceService.getById(99L)).thenThrow(new InvoiceNotFoundException("not found"));

        assertThatThrownBy(() -> invoiceController.getById(99L))
                .isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    void getMostRecent_returnsOk_whenFound() {
        InvoiceResponseDTO dto = new InvoiceResponseDTO(2L, null, null, "BILLED", 19.99);
        when(invoiceService.getMostRecentByUserId("user-1")).thenReturn(dto);

        ResponseEntity<InvoiceResponseDTO> response = invoiceController.getMostRecent("user-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void getForUserBetween_returnsList_whenFound() {
        InvoiceResponseDTO dto = new InvoiceResponseDTO(3L, null, null, "BILLED", 9.99);
        when(invoiceService.getInvoicesForUserBetween("user-1", 0L, 2_000_000L))
                .thenReturn(List.of(dto));

        ResponseEntity<List<InvoiceResponseDTO>> response =
                invoiceController.getForUserBetween("user-1", 0L, 2_000_000L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(dto);
    }
}
