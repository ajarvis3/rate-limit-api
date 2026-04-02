package com.ratelimit.usage.controller;

import com.ratelimit.usage.dto.UsageResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usage")
public class UsageController {

    @GetMapping
    public UsageResponseDTO getUsage(@RequestHeader("X-User-Id") String userId) {
        // For demonstration, we return a static usage response.
        // In a real application, you would fetch this data from a database or another service.
        response.setUserId(userId);
        response.setApiCalls(10); // Example usage count
        response.setDataTransferred(1024L); // Example data transferred in bytes
        return response;

    }
}


