package com.ratelimit.usage.controller;

import com.ratelimit.usage.dto.UsageResponseDTO;
import com.ratelimit.usage.repository.UsageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usage")
public class UsageController {

    private final UsageRepository usageRepository;

    public UsageController(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

    @GetMapping
    public UsageResponseDTO getUsage(@RequestHeader("X-User-Id") String userId,
                                     @RequestParam(name = "start", required = false) Long start,
                                     @RequestParam(name = "end", required = false) Long end) {
        long s = (start == null) ? 0L : start;
        long e = (end == null) ? Long.MAX_VALUE : end;
        long count = usageRepository.countByUserIdAndTimestampBetween(userId, s, e);
        return new UsageResponseDTO(count);
    }
}


