package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.ApiUsage;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsageRepositoryTest {

    @Test
    void apiUsage_holdsInstantTimestampAndRequestCount() {
        ApiUsage a = new ApiUsage("user1", 5L, Instant.ofEpochMilli(12345L));
        assertEquals("user1", a.getUserId());
        assertEquals(5L, a.getRequestCount());
        assertEquals(Instant.ofEpochMilli(12345L), a.getTimestamp());
    }
}


