package com.revelvol.equipmentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revelvol.equipmentservice.dto.EquipmentRequest;
import com.revelvol.equipmentservice.dto.EquipmentResponse;
import com.revelvol.equipmentservice.model.Equipment;
import com.revelvol.equipmentservice.repository.EquipmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
we are going to use test container ( auto boot up docker database for testing compatibility, because if using
in memory some compatibility may be invalid
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class EquipmentServiceApplicationTests {
//integration test for this service

	@Container // test container properties
	final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0-rc6-jammy"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EquipmentRepository equipmentRepository;

	@DynamicPropertySource // add application properties dynamically
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl); // set test uri dynamically
	}

	private EquipmentRequest  getEquipmentRequest() {
		return EquipmentRequest.builder()
				.name("pH meter")
				.description("pH probe to analyze pH to food and non food object")
				.type("handHeld")
				.manufacturer("Eutech")
				.serialNumber("12345")
				.build();

	}

	private Equipment getEquipment(){
		return Equipment.builder()
				.name("pH meter")
				.description("pH probe to analyze pH to food and non food object")
				.type("handHeld")
				.manufacturer("Eutech")
				.serialNumber("12345")
				.build();
	}

	@AfterEach
	private void clearDataBase(){
		equipmentRepository.deleteAll();
	}

	@Test
	void shouldCreateEquipment() throws Exception {

		EquipmentRequest equipmentRequest = getEquipmentRequest();

		String jsonRequest= objectMapper.writeValueAsString(equipmentRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, equipmentRepository.findAll().size());
	}

	@Test
	void shouldGetEquipment() throws Exception {
		equipmentRepository.save(getEquipment());
		MvcResult result =mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments"))
				.andExpect(status().isOk())
						.andReturn();
		String responseBody = result.getResponse().getContentAsString();
		EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);
		Assertions.assertEquals(1, equipmentResponses.length);
		Assertions.assertEquals("pH meter", equipmentResponses[0].getName());
		Assertions.assertEquals("pH probe to analyze pH to food and non food object", equipmentResponses[0].getDescription());
		Assertions.assertEquals(1, equipmentRepository.findAll().size());
	}
}
