package com.ratelimit.subscription.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "subscription")
public class SubscriptionDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    private String planId;
    public SubscriptionDTO(String planId, String userId) {
        this.userId = userId;
        this.planId = planId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPlanId() {
        return planId;
    }
    public void setPlanId(String planId) {
        this.planId = planId;
    }
}