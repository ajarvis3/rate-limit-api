package com.ratelimit.usage.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="UsageAggregate")
public class UsageAggregate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long requestCount;

    private Instant timestampStart;

    private Instant timestampEnd;

    private Instant lastUpdated;

    public UsageAggregate() {}

    public UsageAggregate(String userId, Long requestCount, Instant timestampStart, Instant timestampEnd, Instant lastUpdated) {
        this.userId = userId;
        this.requestCount = requestCount;
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
        this.lastUpdated = lastUpdated;
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

    public Instant getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(Instant timestampStart) {
        this.timestampStart = timestampStart;
    }

    public Instant getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(Instant timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
