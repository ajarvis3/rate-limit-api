package com.ratelimit.usage.controller;

import com.ratelimit.usage.dto.UsageResponseDTO;
import com.ratelimit.usage.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UsageControllerTest {

    private UsageRepository usageRepository;
    private UsageController usageController;

    @BeforeEach
    void setUp() {
        usageRepository = Mockito.mock(UsageRepository.class);
        usageController = new UsageController(usageRepository);
    }

    @Test
    void getUsage_returnsCountFromRepository() {
        Mockito.when(usageRepository.countByUserIdAndTimestampBetween(eq("user1"), any(), any()))
                .thenReturn(42L);

        UsageResponseDTO response = usageController.getUsage("user1", 0L, 100L);

        assertEquals(42L, response.numberOfRequests());
    }
}

