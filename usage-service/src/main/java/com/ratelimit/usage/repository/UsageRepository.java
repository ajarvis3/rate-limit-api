package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.ApiUsage;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends JpaRepository<ApiUsage, Integer> {
    /**
     * If you only need the count, use a count query method which is implemented automatically.
     */
    long countByUserIdAndTimestampBetween(String userId, Instant start, Instant end);

    @Query("SELECT SUM(u.requestCount) FROM ApiUsage u WHERE u.userId = :userId AND u.timestamp >= :start AND u.timestamp <= :end")
    Long sumByUserIdAndTimestampBetween(String userId, Instant start, Instant end);

    @Query("SELECT SUM(u.requestCount) FROM ApiUsage u WHERE u.userId = :userId AND u.timestamp >= :timestamp")
    Long sumByUserIdAndTimestampSince(String userId, Instant timestamp);
}
