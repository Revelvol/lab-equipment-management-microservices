package com.revelvol.maintenanceservice.config;

import com.revelvol.maintenanceservice.event.MaintenanceTicketPlacedEventProducer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaMaintenanceTicketConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }
    @Bean
    public ProducerFactory<String, MaintenanceTicketPlacedEventProducer> producerFactory() {

        //producer config
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "notification:com.revelvol.maintenanceservice.event.MaintenanceTicketPlacedEventProducer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notificationTopic", 1, (short) 1);
    }

    @Bean
    public KafkaTemplate<String, MaintenanceTicketPlacedEventProducer> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}
