package com.revelvol.maintenanceservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties) {
        return new LoadBalancerClientFactory(properties) {
            @Override public AnnotationConfigApplicationContext createContext(String name) {
                ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) super.createContext(name);
                Thread.currentThread().setContextClassLoader(originalClassLoader);
                return context;
            }
        };
    }
    @Bean // @LoadBalanced because there might be multiple service
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();
    }
}
