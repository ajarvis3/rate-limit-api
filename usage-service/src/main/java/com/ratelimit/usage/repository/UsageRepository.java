package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends JpaRepository<ApiUsage, Integer> {
    /**
     * If you only need the count, use a count query method which is implemented automatically.
     */
    long countByUserIdAndTimestampBetween(String userId, Long start, Long end);
}
