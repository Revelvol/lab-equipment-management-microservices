package com.revelvol.maintenanceservice;

import com.revelvol.maintenanceservice.event.MaintenanceTicketPlacedEventProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class MaintenanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaintenanceServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner (KafkaTemplate<String, MaintenanceTicketPlacedEventProducer> kafkaTemplate) {
		return args -> {
			//test whether it send the data or not
			kafkaTemplate.send("notificationTopic", new MaintenanceTicketPlacedEventProducer(12123132L,"test"));
		};
	}

}
