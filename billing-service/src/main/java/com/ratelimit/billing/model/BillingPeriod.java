package com.ratelimit.billing.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "billing_period")
public class BillingPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private Instant periodStart;

    private Instant periodEnd;

    private boolean invoiceCreated;

    public BillingPeriod() {}

    public BillingPeriod(UUID userId, Instant periodStart, Instant periodEnd) {
        this.userId = userId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.invoiceCreated = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public boolean isInvoiceCreated() {
        return invoiceCreated;
    }

    public void setInvoiceCreated(boolean invoiceCreated) {
        this.invoiceCreated = invoiceCreated;
    }
}
