package com.ratelimit.billing.dto;

// Moved to dto-common module
public record InvoiceResponseDTO(Long id, String invoiceNumber, Long orderId, String status, Double totalAmount) {}
