package com.revelvol.apigateway.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ObjectMapper objectMapper;

    private String extractTokenFromRequest(ServerHttpRequest request) {

        return null;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // extract data from the request
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getPath();
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String token = authHeader.substring(7);

        if (requestUri.startsWith("/api/v1/authentication") ) {
            // Allow access to JWT service's /register and /authenticate endpoints without token validation.
            return chain.filter(exchange);
        }
        // Other API endpoints, perform token validation with the JWT service.
        // Call JWT service to validate the token.
        // You can use WebClient or RestTemplate to make the HTTP call.
        // For simplicity, let's assume we have a method validateTokenWithJwtService() that returns a boolean.
        boolean isTokenValid = validateTokenWithAuthenticationServer(token);

        if (isTokenValid) {
            // Token is valid, proceed with the request.
            return chain.filter(exchange);
        } else {
            // Token is invalid, return an error response.
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    private Boolean batchCheckIsSkuValid(List<String> skuCodeList) {
        // Check all List of SkuCode is valid and exist on the equipment service
        return webClientBuilder.build()
                .get()
                .uri("http://equipment-service/api/v1/equipments/sku",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodeList).build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
    private boolean validateTokenWithAuthenticationServer(String token) {

        return true;
    }
}

