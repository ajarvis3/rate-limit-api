package com.ratelimit.apigateway.filter;

import com.ratelimit.apigateway.service.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
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

	RateLimitService rateLimitService;

	public RateLimitFilter(RateLimitService rateLimitService) {
		this.rateLimitService = rateLimitService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("RateLimitWebFilter: before chain");
		if (rateLimitService.checkRateLimit(exchange)) {
			exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
			return exchange.getResponse().setComplete();
		}
		// Add non-blocking rate limiting logic here (use reactive Redis client etc.)
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 1;
	}
}
