package com.ratelimit.billing.repository;

import com.ratelimit.billing.model.BillingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillingPeriodRepository extends JpaRepository<BillingPeriod, UUID> {
    List<BillingPeriod> findByInvoiceCreatedFalseAndPeriodEndBefore(Instant now);
}
