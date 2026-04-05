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

import java.util.Collection;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	private static final List<String> KNOWN_PLANS = List.of("ENTERPRISE", "PRO", "FREE");

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
					String planTier = extractPlanTier(token);
					return userServiceClient.getOrCreateUser(keycloakId, planTier)
							.flatMap(user -> {
								ServerHttpRequest mutatedRequest = exchange.getRequest()
										.mutate()
										.header("X-User-Id", keycloakId)
										.header("X-User-Plan", planTier)
										.build();
								return chain.filter(exchange.mutate().request(mutatedRequest).build());
							});

				})
				.switchIfEmpty(Mono.defer(() -> {
					log.debug("No JwtAuthenticationToken present on the exchange; continuing without X-User-Id header");
					return chain.filter(exchange);
				}));
	}

	private String extractPlanTier(JwtAuthenticationToken token) {
		try {
			Object realmAccess = token.getToken().getClaims().get("realm_access");
			if (realmAccess instanceof java.util.Map<?, ?> realmMap) {
				Object roles = realmMap.get("roles");
				if (roles instanceof Collection<?> roleList) {
					// KNOWN_PLANS is ordered by precedence (ENTERPRISE > PRO > FREE).
					// The highest-priority plan found in the user's roles is returned.
					// If a user has multiple plan roles, the first match in KNOWN_PLANS wins.
					for (String plan : KNOWN_PLANS) {
						if (roleList.contains(plan)) {
							return plan;
						}
					}
				}
			}
		} catch (Exception e) {
			log.debug("Could not extract plan tier from JWT realm roles", e);
		}
		return "FREE";
	}

    @Override
    public int getOrder() {
		return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 1;
    }
}
