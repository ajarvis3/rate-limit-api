package com.ratelimit.usage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usage")
public class ApiUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private String requestId;

    private Long timestamp;
    
    public ApiUsage() {
    }

    public ApiUsage(String userId, String requestId, Long timestamp) {
        this.userId = userId;
        this.requestId = requestId;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
