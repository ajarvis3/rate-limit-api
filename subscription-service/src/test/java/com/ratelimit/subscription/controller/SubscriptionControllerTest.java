package com.ratelimit.subscription.controller;

import com.ratelimit.subscription.dto.SubscriptionDTO;
import com.ratelimit.subscription.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SubscriptionControllerTest {

    private SubscriptionService subscriptionService;
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        subscriptionService = Mockito.mock(SubscriptionService.class);
        subscriptionController = new SubscriptionController(subscriptionService);
    }

    @Test
    void getSubscription_returnsDto() {
        SubscriptionDTO dto = new SubscriptionDTO("user-1", "PREMIUM");
        when(subscriptionService.getSubscription("user-1")).thenReturn(dto);

        SubscriptionDTO result = subscriptionController.getSubscription("user-1");

        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.planId()).isEqualTo("PREMIUM");
    }
}
