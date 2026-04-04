package com.ratelimit.subscription.service;

import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.model.Subscription;
import com.ratelimit.subscription.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubscriptionService {
    
    public SubscriptionRepository subscriptionRepository;
    
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }
    
    public SubscriptionDTO getSubscription(String userId) {
        Subscription subscription = subscriptionRepository.getSubscriptionByUserId(userId);
        if (subscription == null) {
            // Throwing a ResponseStatusException with NOT_FOUND results in a 404 response
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found for user: " + userId);
        }
        // return DTO when found
        return new SubscriptionDTO(userId, subscription.getLevel());
    }
}
