package com.revelvol.maintenanceservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean // @LoadBalanced because there might be multiple service
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();
    }
}
