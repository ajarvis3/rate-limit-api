package com.ratelimit.dunning.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dunning_record")
public class DunningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Long invoiceId;
    private Double amount;
    private long failedAt;
    private long retryAfter;
    private boolean retried;

    public DunningRecord() {}

    public DunningRecord(String userId, Long invoiceId, Double amount, long failedAt, long retryAfter) {
        this.userId = userId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.failedAt = failedAt;
        this.retryAfter = retryAfter;
        this.retried = false;
    }

    public Long getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public long getFailedAt() { return failedAt; }
    public void setFailedAt(long failedAt) { this.failedAt = failedAt; }

    public long getRetryAfter() { return retryAfter; }
    public void setRetryAfter(long retryAfter) { this.retryAfter = retryAfter; }

    public boolean isRetried() { return retried; }
    public void setRetried(boolean retried) { this.retried = retried; }
}
