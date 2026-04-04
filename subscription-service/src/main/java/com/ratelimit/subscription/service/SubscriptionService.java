package com.ratelimit.subscription.service;

import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.model.Subscription;
import com.ratelimit.subscription.repository.SubscriptionRepository;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import com.ratelimit.subscription.producer.SubscriptionBillingProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubscriptionService {
    
    public SubscriptionRepository subscriptionRepository;
    private final SubscriptionBillingProducer producer;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionBillingProducer producer) {
        this.subscriptionRepository = subscriptionRepository;
        this.producer = producer;
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

    public SubscriptionBillingDTO getSubscription(UsageAggregateBillingMessage uabm) {
        Subscription subscription = subscriptionRepository.getSubscriptionByUserId(uabm.userId());
        SubscriptionBillingDTO dto = new SubscriptionBillingDTO(
                uabm.userId(),
                uabm.requestCount(),
                uabm.lastUpdated(),
                uabm.periodStart(),
                uabm.periodEnd(),
                subscription != null ? subscription.getLevel() : "FREE"
        );
        // send billing DTO to billing service
        try {
            producer.sendSubscriptionBilling(dto);
        } catch (Exception e) {
            // log and continue; subscription retrieval should not fail if billing send fails
            System.err.println("Failed to send subscription billing message: " + e.getMessage());
        }
        return dto;
    }
}
