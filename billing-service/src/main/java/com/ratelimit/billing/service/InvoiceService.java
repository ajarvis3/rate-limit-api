package com.ratelimit.billing.service;

import com.ratelimit.billing.dto.InvoiceResponseDTO;
import com.ratelimit.billing.model.Invoice;
import com.ratelimit.billing.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository repo;

    private final BillingService billingService;

    public InvoiceService(InvoiceRepository repo, BillingService billingService) {
        this.repo = repo;
        this.billingService = billingService;
    }

    public void createInvoice(String userId, Long requestCount, long billedAt, String subscription) {
        Invoice invoice = new Invoice(userId, billedAt, requestCount.doubleValue(), subscription);
        repo.save(invoice);
        billingService.processInvoice(invoice);
    }

    public InvoiceResponseDTO getById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new com.ratelimit.billing.exception.InvoiceNotFoundException("Invoice not found id=" + id));
    }

    public InvoiceResponseDTO getMostRecentByUserId(String userId) {
        return repo.findTopByUserIdOrderByBilledAtDesc(userId)
                .map(this::toDto)
                .orElseThrow(() -> new com.ratelimit.billing.exception.InvoiceNotFoundException("No invoices found for user=" + userId));
    }

    public List<InvoiceResponseDTO> getInvoicesForUserBetween(String userId, long start, long end) {
        List<Invoice> list = repo.findByUserIdAndBilledAtBetween(userId, start, end);
        if (list == null || list.isEmpty()) {
            throw new com.ratelimit.billing.exception.InvoiceNotFoundException("No invoices found for user=" + userId + " between " + start + " and " + end);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private InvoiceResponseDTO toDto(Invoice i) {
        // adapt fields: invoiceNumber/orderId/status not present — reuse id and amount fields
        return new InvoiceResponseDTO(i.getId(), null, null, "BILLED", i.getAmount());
    }
}

