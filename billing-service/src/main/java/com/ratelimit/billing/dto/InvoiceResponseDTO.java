package com.ratelimit.billing.dto;

public record InvoiceResponseDTO(
        Long id,
        String invoiceNumber,
        Long orderId,
        String status,
        Double totalAmount
) {}