package com.ratelimit.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2)
public class RateLimitFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("RateLimitWebFilter: before chain");
		// Add non-blocking rate limiting logic here (use reactive Redis client etc.)
		return chain.filter(exchange)
				.doFinally(signal -> log.info("RateLimitWebFilter: after chain"));
	}

	@Override
	public int getOrder() {
		return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 1;
	}
}
