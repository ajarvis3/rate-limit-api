package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.UsageAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsageAggregateRepository extends JpaRepository<UsageAggregate, Long> {
    UsageAggregate findByUserId(String userId);

    @Query("SELECT a FROM UsageAggregate a WHERE a.lastUpdated < a.timestampEnd")
    List<UsageAggregate> findActiveAggregates();
}
