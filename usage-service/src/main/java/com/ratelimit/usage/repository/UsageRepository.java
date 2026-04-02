package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Integer> {
}
