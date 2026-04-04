package com.ratelimit.usage.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "usage")
public class ApiUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private Long requestCount;

    private Instant timestamp;
    
    public ApiUsage() {
    }

    public ApiUsage(String userId, Long requestCount, Instant timestamp) {
        this.userId = userId;
        this.requestCount = requestCount;
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

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
