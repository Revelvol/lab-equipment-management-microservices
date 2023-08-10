package com.revelvol.apigateway.config;

import com.revelvol.apigateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ApiGatewayConfig {

    AuthenticationFilter authenticationFilter;

    public ApiGatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    @Profile("!docker")
    public RouteLocator defaultRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("equipment-service", r -> r.path("/api/v1/equipments/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://equipment-service"))
                .route("maintenance-ticket-service", r -> r.path("/api/v1/maintenance-ticket/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://maintenance-ticket-service"))
                .route("progress-service", r -> r.path("/api/v1/progresses/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://progress-service"))
                .route("discovery-server", r -> r.path("/eureka/web")
                        .filters(f -> {
                            f.setPath("/");
                            return f;
                        })
                        .uri("http://localhost:8761/"))
                .route("discovery-server-static", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761/"))
                .route("authentication-server", r -> r.path("/api/v1/auth/**")
                        //.filters(f -> f.setPath("/"))
                        .uri("lb://authentication-server"))
                .build();
    }

    @Bean
    @Profile("docker")
    public RouteLocator dockerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("equipment-service", r -> r.path("/api/v1/equipments/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://equipment-service"))
                .route("maintenance-ticket-service", r -> r.path("/api/v1/maintenance-ticket/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://maintenance-ticket-service"))
                .route("progress-service", r -> r.path("/api/v1/progresses/**")
                        .filters(f -> {
                            f.filter( authenticationFilter);
                            return f;
                        })
                        .uri("lb://progress-service"))
                .route("discovery-server", r -> r.path("/eureka/web")
                        .filters(f -> {
                            f.setPath("/");
                            return f;
                        })
                        .uri("http://discovery-server:8761/"))
                .route("discovery-server-static", r -> r.path("/eureka/**")
                        .uri("http://discovery-server:8761/"))
                .route("authentication-server", r -> r.path("/api/v1/auth/**")
                        //.filters(f -> f.setPath("/"))
                        .uri("lb://authentication-server"))
                .build();
    }




}


