package com.ratelimit.usage.repository;

import com.ratelimit.usage.model.ApiUsage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsageRepositoryTest {

    @Autowired
    private UsageRepository usageRepository;

    @Test
    void countByUserIdAndTimestampBetween_countsCorrectly() {
        // given
        ApiUsage a1 = new ApiUsage("user1", "req1", 100L);
        ApiUsage a2 = new ApiUsage("user1", "req2", 200L);
        ApiUsage a3 = new ApiUsage("user2", "req3", 150L);
        usageRepository.save(a1);
        usageRepository.save(a2);
        usageRepository.save(a3);

        // when
        long count = usageRepository.countByUserIdAndTimestampBetween("user1", 50L, 150L);

        // then
        assertThat(count).isEqualTo(1L);
    }
}


