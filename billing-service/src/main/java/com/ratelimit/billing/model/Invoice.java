package com.ratelimit.billing.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Instant billedAt;

    private Double amount;

    public Invoice() {}

    public Invoice(String userId, Instant billedAt, Double amount) {
        this.userId = userId;
        this.billedAt = billedAt;
        this.amount = amount;
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

    public Instant getBilledAt() {
        return billedAt;
    }

    public void setBilledAt(Instant billedAt) {
        this.billedAt = billedAt;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

