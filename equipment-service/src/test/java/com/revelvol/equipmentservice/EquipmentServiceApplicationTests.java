package com.revelvol.equipmentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revelvol.equipmentservice.dto.ApiError;
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
import org.springframework.http.HttpStatus;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri",
                mongoDBContainer::getReplicaSetUrl); // set test uri dynamically
    }

    private EquipmentRequest getEquipmentRequest() {
        return EquipmentRequest.builder().skuCode("pH meter").description(
                "pH probe to analyze pH to food and non food object").type("handHeld").manufacturer("Eutech").serialNumber(
                "12345").model("test model").build();

    }
    private EquipmentRequest getInvalidEquipmentRequestNoName() {
        return EquipmentRequest.builder().description(
                "pH probe to analyze pH to food and non food object").type("handHeld").manufacturer("Eutech").serialNumber(
                "12345").model("test model").build();

    }

    private EquipmentRequest getPutEquipmentRequest() {
        return EquipmentRequest.builder().skuCode("pH meter updated").description("pH probe to analyze pH to non food").type(
                "stand in machine").manufacturer("Sigmatech").serialNumber("54321").model("test model updated").build();

    }

    private EquipmentRequest getPatchEquipmentRequestPartialNoName() {
        return EquipmentRequest.builder().description("pH probe to analyze pH to non food").type(
                "stand in machine").manufacturer("Sigmatech").serialNumber("54321").model("test model updated").build();

    }

    private Equipment getEquipment() {
        return Equipment.builder().skuCode("pH meter").description("pH probe to analyze pH to food and non food object").type(
                "handHeld").manufacturer("Eutech").serialNumber("12345").model("test model").build();
    }

    @AfterEach
    public void clearDataBase() {
        equipmentRepository.deleteAll();
    }

    @Test
    void shouldCreateEquipment() throws Exception {

        EquipmentRequest equipmentRequest = getEquipmentRequest();

        String jsonRequest = objectMapper.writeValueAsString(equipmentRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isCreated());

        Assertions.assertEquals(1, equipmentRepository.findAll().size());

    }

    @Test
    void shouldNotCreateEquipmentNoName() throws Exception {
        EquipmentRequest equipmentRequest = getInvalidEquipmentRequestNoName();

        String jsonRequest = objectMapper.writeValueAsString(equipmentRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        ApiError response = objectMapper.readValue(responseBody, ApiError.class);
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("skuCode must not be null");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        Assertions.assertEquals(expectedErrors, response.getErrorsDetails());
        Assertions.assertEquals(0, equipmentRepository.findAll().size());
    }

    @Test
    void shouldNotCreateEquipmentDuplicateSkuCode() throws Exception {

        EquipmentRequest equipmentRequest = getEquipmentRequest();

        String jsonRequest = objectMapper.writeValueAsString(equipmentRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isCreated());

        Assertions.assertEquals(1, equipmentRepository.findAll().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError());

        Assertions.assertEquals(1, equipmentRepository.findAll().size());



    }

    @Test
    void shouldGetEquipment() throws Exception {
        equipmentRepository.save(getEquipment());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);
        Assertions.assertEquals(1, equipmentResponses.length);
        Assertions.assertEquals("pH meter", equipmentResponses[0].getSkuCode());
        Assertions.assertEquals("pH probe to analyze pH to food and non food object",
                equipmentResponses[0].getDescription());
        Assertions.assertEquals(1, equipmentRepository.findAll().size());
    }

    @Test
    void shouldGetEquipmentById() throws Exception {
        equipmentRepository.save(getEquipment());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);
        MvcResult resultIndividual = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments/" + equipmentResponses[0].getId())).andExpect(
                status().isOk()).andReturn();
        String individualResponseBody = resultIndividual.getResponse().getContentAsString();
        EquipmentResponse individualEquipmentResponse = objectMapper.readValue(individualResponseBody,
                EquipmentResponse.class);

        Assertions.assertEquals(individualEquipmentResponse, equipmentResponses[0]);
    }

    @Test
    void shouldError404GetEquipmentById() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments/1")).andExpect(status().isNotFound()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        ApiError errorResponse = objectMapper.readValue(responseBody, ApiError.class);
        Assertions.assertEquals(404, errorResponse.getCode());
        Assertions.assertEquals("Not Found", errorResponse.getMessage());

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("Equipment not found");
        Assertions.assertEquals(expectedErrors,errorResponse.getErrorsDetails());
    }

    @Test
    void shouldPutIndividualEquipment() throws Exception {
        equipmentRepository.save(getEquipment());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);

        String equipmentId = equipmentResponses[0].getId();
        EquipmentRequest putEquipmentRequest = getPutEquipmentRequest();
        String jsonRequest = objectMapper.writeValueAsString(putEquipmentRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/equipments/" + equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpectAll(status().isOk(),
                        jsonPath("$.skuCode").value("pH meter updated"),
                        jsonPath("$.description").value("pH probe to analyze pH to non food"),
                        jsonPath("$.type").value("stand in machine"),
                        jsonPath("$.manufacturer").value("Sigmatech"),
                        jsonPath("$.serialNumber").value("54321"),
                        jsonPath("$.model").value("test model updated")

                );
        Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        Assertions.assertEquals(1, equipmentRepository.findAll().size());

    }

    @Test
    void shouldPatchIndividualEquipment() throws Exception {
        equipmentRepository.save(getEquipment());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);

        String equipmentId = equipmentResponses[0].getId();
        EquipmentRequest putEquipmentRequest = getPatchEquipmentRequestPartialNoName();
        String jsonRequest = objectMapper.writeValueAsString(putEquipmentRequest);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/equipments/" + equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpectAll(status().isOk(),
                        jsonPath("$.skuCode").value("pH meter"),// this name is not changing
                        jsonPath("$.description").value("pH probe to analyze pH to non food"),
                        jsonPath("$.type").value("stand in machine"),
                        jsonPath("$.manufacturer").value("Sigmatech"),
                        jsonPath("$.serialNumber").value("54321"),
                        jsonPath("$.model").value("test model updated")

                );
        Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        Assertions.assertEquals(1, equipmentRepository.findAll().size());
    }


    @Test
    void shouldDeleteIndividualEquipment() throws Exception {
        equipmentRepository.save(getEquipment());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);

        String equipmentId = equipmentResponses[0].getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/equipments/" + equipmentId))
                .andExpectAll(status().isOk());

        Assertions.assertEquals(0, equipmentRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteIndividualEquipmentNotFound() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        EquipmentResponse[] equipmentResponses = objectMapper.readValue(responseBody, EquipmentResponse[].class);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/equipments/1" ))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.message").value("Not Found"),
                        jsonPath("$.errorsDetails").value("Equipment not found"));

        Assertions.assertEquals(0, equipmentRepository.findAll().size());
    }
    @Test
    void shouldNotPutIndividualEquipmentNoResponseBody() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();



        result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/equipments/1" ))
                .andExpectAll(status().is4xxClientError()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        ApiError apiErrorResponse =objectMapper.readValue(responseBody, ApiError.class);
        Assertions.assertEquals(400, apiErrorResponse.getCode());
        Assertions.assertEquals("Invalid request body", apiErrorResponse.getMessage());

        Assertions.assertEquals(0, equipmentRepository.findAll().size());
    }
    @Test
    void shouldNotPatchIndividualEquipmentNotResponseBody() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")).andExpect(status().isOk()).andReturn();



        result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/equipments/1" ))
                .andExpectAll(status().is4xxClientError()).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiError apiErrorResponse =objectMapper.readValue(responseBody, ApiError.class);
        Assertions.assertEquals(400, apiErrorResponse.getCode());
        Assertions.assertEquals("Invalid request body", apiErrorResponse.getMessage());


        Assertions.assertEquals(0, equipmentRepository.findAll().size());
    }
}
