package com.ratelimit.subscription.service;

import com.ratelimit.subscription.dto.SubscriptionBillingDTO;
import com.ratelimit.subscription.dto.SubscriptionCreatedEvent;
import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.model.Subscription;
import com.ratelimit.subscription.producer.SubscriptionBillingProducer;
import com.ratelimit.subscription.producer.SubscriptionEventProducer;
import com.ratelimit.subscription.repository.SubscriptionRepository;
import com.ratelimit.usage.dto.UsageAggregateBillingMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SubscriptionService {

    public SubscriptionRepository subscriptionRepository;
    private final SubscriptionBillingProducer billingProducer;
    private final SubscriptionEventProducer eventProducer;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               SubscriptionBillingProducer billingProducer,
                               SubscriptionEventProducer eventProducer) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingProducer = billingProducer;
        this.eventProducer = eventProducer;
    }

    public SubscriptionDTO getSubscription(String userId) {
        Subscription subscription = subscriptionRepository.getSubscriptionByUserId(UUID.fromString(userId));
        if (subscription == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found for user: " + userId);
        }
        return new SubscriptionDTO(userId, subscription.getLevel());
    }

    public SubscriptionBillingDTO getSubscription(UsageAggregateBillingMessage uabm) {
        Subscription subscription = subscriptionRepository.getSubscriptionByUserId(UUID.fromString(uabm.userId()));
        SubscriptionBillingDTO dto = new SubscriptionBillingDTO(
                uabm.userId(),
                uabm.requestCount(),
                uabm.lastUpdated(),
                uabm.periodStart(),
                uabm.periodEnd(),
                subscription != null ? subscription.getLevel() : "FREE"
        );
        try {
            billingProducer.sendSubscriptionBilling(dto);
        } catch (Exception e) {
            System.err.println("Failed to send subscription billing message: " + e.getMessage());
        }
        return dto;
    }

    public Subscription createSubscription(UUID userId, String planId) {
        Instant now = Instant.now();
        Instant periodEnd = now.plus(30, ChronoUnit.DAYS);
        Subscription subscription = new Subscription(userId, planId, "ACTIVE", now, periodEnd);
        Subscription saved = subscriptionRepository.save(subscription);
        eventProducer.sendSubscriptionCreated(new SubscriptionCreatedEvent(
                userId, saved.getId(), now, periodEnd));
        return saved;
    }

    public void renewSubscription(UUID userId, UUID subscriptionId) {
        subscriptionRepository.findByUserId(userId).ifPresent(subscription -> {
            Instant newStart = subscription.getCurrentPeriodEnd() != null
                    ? subscription.getCurrentPeriodEnd()
                    : Instant.now();
            Instant newEnd = newStart.plus(30, ChronoUnit.DAYS);
            subscription.setCurrentPeriodStart(newStart);
            subscription.setCurrentPeriodEnd(newEnd);
            subscriptionRepository.save(subscription);
            eventProducer.sendSubscriptionCreated(new SubscriptionCreatedEvent(
                    userId, subscription.getId(), newStart, newEnd));
        });
    }

    public void suspendSubscription(UUID userId) {
        subscriptionRepository.findByUserId(userId).ifPresent(subscription -> {
            subscription.setStatus("SUSPENDED");
            subscriptionRepository.save(subscription);
        });
    }
}
