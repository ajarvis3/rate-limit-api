package com.ratelimit.apigateway.filter;

import com.ratelimit.apigateway.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

	private final UserServiceClient userServiceClient;

    public AuthenticationFilter(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }


    @Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("AuthenticationWebFilter: before chain");
		return exchange.getPrincipal()
				.cast(JwtAuthenticationToken.class)
				.flatMap(token -> {
					String keycloakId = token.getToken().getSubject();
					return userServiceClient.getOrCreateUser(keycloakId)
							.flatMap(user -> {
								ServerHttpRequest mutatedRequest = exchange.getRequest()
										.mutate()
										.header("X-User-Id", keycloakId)
										.build();
								return chain.filter(exchange.mutate().request(mutatedRequest).build());
							});

				})
				.switchIfEmpty(Mono.defer(() -> {
					log.debug("No JwtAuthenticationToken present on the exchange; continuing without X-User-Id header");
					return chain.filter(exchange);
				}));
	}

    @Override
    public int getOrder() {
		return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 1;
    }
}
