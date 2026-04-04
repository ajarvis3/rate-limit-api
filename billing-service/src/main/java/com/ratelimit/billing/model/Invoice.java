package com.ratelimit.billing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private long billedAt;

    private Double amount;
    
    private String subscription;
    
    private String status;

    public Invoice() {
    }

    public Invoice(String userId, long billedAt, Double amount, String subscription) {
        this.userId = userId;
        this.billedAt = billedAt;
        this.amount = amount;
        this.subscription = subscription;
        this.status = "CREATED";
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getBilledAt() {
        return billedAt;
    }

    public void setBilledAt(long billedAt) {
        this.billedAt = billedAt;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

