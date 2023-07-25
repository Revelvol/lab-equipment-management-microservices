package com.revelvol.equipmentservice;

import com.revelvol.equipmentservice.model.Equipment;
import com.revelvol.equipmentservice.repository.EquipmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class EquipmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquipmentServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadTestData(EquipmentRepository equipmentRepository, MongoTemplate mongoTemplate){
		return args -> {
			//drop the database prior
			mongoTemplate.dropCollection(Equipment.class);

			//insert  new default test equipment
			Equipment testEquipment = new Equipment();
			testEquipment.setSkuCode("E12345");
			equipmentRepository.save(testEquipment);
		};
	}

}
