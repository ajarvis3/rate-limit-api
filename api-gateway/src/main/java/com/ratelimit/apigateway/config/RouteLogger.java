package com.ratelimit.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLogger {

    private static final Logger log = LoggerFactory.getLogger(RouteLogger.class);

    // Log routes after the application is ready so the RouteLocator has been fully initialized
    @Bean
    public ApplicationListener<ApplicationReadyEvent> logRoutes(RouteLocator locator) {
        return event -> locator.getRoutes().collectList().subscribe(list -> {
            if (list == null || list.isEmpty()) {
                log.info("No gateway routes registered at startup");
                return;
            }
            list.forEach(route -> log.info("Registered route: id={} uri={}", route.getId(), route.getUri()));
        }, err -> log.warn("Failed to list gateway routes", err));
    }
}



