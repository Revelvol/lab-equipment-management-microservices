package com.revelvol.apigateway.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.revelvol.apigateway.dto.ApiError;
import com.revelvol.apigateway.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;

    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // extract data from the request
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getPath();
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (requestUri.startsWith("/api/v1/authentication")) {
            // Allow access to JWT service's /register and /authenticate endpoints without token validation.
            return chain.filter(exchange);
        }
        // Other API endpoints, perform token validation with the JWT service.
        // Call JWT service to validate the token.
        // if token is valid proceed, if the server response 4xx, response that
        return validateTokenWithAuthenticationServer(token)
                .flatMap(response -> {
                    String statusCode = response.getStatusCode().toString();

                    if (statusCode.startsWith("2")) {
                        // Token is valid, proceed with the request.
                        return chain.filter(exchange);
                    } else if (statusCode.startsWith("4")) {
                        // Additional response handling for 4xx errors.
                        // You can access the response body using response.getBody().

                        //log.warn("Token is invalid: {}:"+response.getBody().toString());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();


                    } else {
                        // Token is invalid, return an error response.
                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    }
                });

    }

    //call the authentication server, if its return object then it is valid
    private Mono<ResponseEntity<?>> validateTokenWithAuthenticationServer(String token) {
        String bearerToken = "Bearer " + token;

        return webClientBuilder.build()
                .get()
                .uri("http://authentication-server/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // Token is valid, map the response body to UserResponse and create ResponseEntity<UserResponse>.
                        return response.bodyToMono(UserResponse.class)
                                .map(userResponse -> ResponseEntity.ok(userResponse));
                    } else {
                        // Map the response body to ApiError and create ResponseEntity<ApiError>.
                        return response.bodyToMono(ApiError.class)
                                .map(apiError -> ResponseEntity.status(response.statusCode()).body(apiError));
                    }
                });
    }
}

