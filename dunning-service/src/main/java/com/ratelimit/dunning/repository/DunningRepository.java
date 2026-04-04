package com.ratelimit.dunning.repository;

import com.ratelimit.dunning.model.DunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DunningRepository extends JpaRepository<DunningRecord, Long> {

    List<DunningRecord> findByRetriedFalseAndRetryAfterLessThanEqual(long retryAfter);
}
