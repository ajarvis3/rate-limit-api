package com.ratelimit.usage.service;

import com.ratelimit.usage.dto.UsageResponseDTO;
import com.ratelimit.usage.repository.UsageRepository;

import java.sql.Timestamp;

public class UsageService {

    UsageRepository usageRepository;

    public UsageService(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

    public UsageResponseDTO getUsage(String userId) {
        long start = 0;
        long end = System.currentTimeMillis(); // temporary until a database is up
        long numberOfRequests = usageRepository.countByUserIdAndTimestampBetween(userId, start, end);
        return new UsageResponseDTO(numberOfRequests);
    }
}
