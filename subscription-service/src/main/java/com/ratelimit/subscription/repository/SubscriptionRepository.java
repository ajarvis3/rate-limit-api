package com.ratelimit.subscription.repository;

import com.ratelimit.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Subscription getSubscriptionByUserId(UUID userId);
    Optional<Subscription> findByUserId(UUID userId);
}