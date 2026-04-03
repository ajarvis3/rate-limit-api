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

    public InvoiceService(InvoiceRepository repo) {
        this.repo = repo;
    }

    public Optional<InvoiceResponseDTO> getById(Long id) {
        return repo.findById(id).map(this::toDto);
    }

    public Optional<InvoiceResponseDTO> getMostRecentByKeycloakId(String keycloakId) {
        return Optional.ofNullable(repo.findTopByUserIdOrderByBilledAtDesc(keycloakId)).map(this::toDto);
    }

    public List<InvoiceResponseDTO> getInvoicesForUserBetween(String userId, long start, long end) {
        return repo.findByUserIdAndBilledAtBetween(userId, start, end).stream().map(this::toDto).collect(Collectors.toList());
    }

    private InvoiceResponseDTO toDto(Invoice i) {
        // adapt fields: invoiceNumber/orderId/status not present — reuse id and amount fields
        return new InvoiceResponseDTO(i.getId(), null, null, "BILLED", i.getAmount());
    }
}

