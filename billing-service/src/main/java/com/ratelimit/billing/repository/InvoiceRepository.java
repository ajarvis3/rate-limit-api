package com.ratelimit.billing.repository;

import com.ratelimit.billing.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    // find most recent invoice for a user
    Optional<Invoice> findTopByUserIdOrderByBilledAtDesc(String userId);

    // find invoices for a user between billedAt (inclusive)
    List<Invoice> findByUserIdAndBilledAtBetween(String userId, long start, long end);
}