package com.revelvol.config;

import com.revelvol.event.MaintenanceTicketPlacedEventConsumer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaNotificationConfig {
    private final String bootstrapServers ="localhost:29092";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }



    private Map<String, Object> consumerConfigs() {
        //consumer config
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
        consumerProps.put(JsonDeserializer.TYPE_MAPPINGS, "notification:com.revelvol.event.MaintenanceTicketPlacedEventConsumer");
        return consumerProps;
    }
    @Bean
    public ConsumerFactory<String, MaintenanceTicketPlacedEventConsumer> consumerFactory() {


        /*configProps.put("spring.json.type.mapping",
                "event:com.revelvol.maintenanceservice.event.MaintenanceTicketPlacedEventProducer");*/



        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }



    @Bean
    KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, MaintenanceTicketPlacedEventConsumer>> kafkaListenerContainerFactory() {

        //enable multithreaded consumption
        ConcurrentKafkaListenerContainerFactory<String, MaintenanceTicketPlacedEventConsumer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    };

}
