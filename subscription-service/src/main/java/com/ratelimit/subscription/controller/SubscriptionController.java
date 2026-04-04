package com.ratelimit.subscription.controller;

import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.repository.SubscriptionRepository;
import com.ratelimit.subscription.service.SubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionController {

    private SubscriptionService susbcriptionService;

    public SubscriptionController(SubscriptionService susbcriptionService) {
        this.susbcriptionService = susbcriptionService;
    }

    @GetMapping("/subscription")
    public SubscriptionDTO getSubscription(String id) {
        return susbcriptionService.getSubscription(id);
    }

}