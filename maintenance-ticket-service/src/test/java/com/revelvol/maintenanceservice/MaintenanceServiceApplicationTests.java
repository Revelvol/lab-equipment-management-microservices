package com.revelvol.maintenanceservice;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revelvol.maintenanceservice.dto.ApiError;
import com.revelvol.maintenanceservice.dto.MaintenanceEquipmentItemsDto;
import com.revelvol.maintenanceservice.dto.MaintenanceTicketRequest;
import com.revelvol.maintenanceservice.dto.MaintenanceTicketResponse;
import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import com.revelvol.maintenanceservice.repository.MaintenanceTicketRepository;
import com.revelvol.maintenanceservice.service.MaintenanceTicketService;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MaintenanceServiceApplicationTests {
    @Container
    final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3").withDatabaseName(
            "maintenance-service-test-db").withUsername("admin-test").withPassword("password-test");


    @DynamicPropertySource // add application properties dynamically
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    private MockMvc mockMvc;


    private ObjectMapper objectMapper;

    private MaintenanceTicketRepository maintenanceTicketRepository;


    @Autowired
    public MaintenanceServiceApplicationTests(MockMvc mockMvc, ObjectMapper objectMapper, MaintenanceTicketRepository maintenanceTicketRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.maintenanceTicketRepository = maintenanceTicketRepository;
    }

    @AfterEach
    public void clearDataBase() {
        maintenanceTicketRepository.deleteAll();
    }


    private MaintenanceTicket createMaintenanceTicket() {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();
        maintenanceTicket.setDescription("Equipment maintenance");
        maintenanceTicket.setIsCompleted(Boolean.FALSE);

        MaintenanceEquipmentItem equipmentItem = new MaintenanceEquipmentItem();
        equipmentItem.setEquipmentSkuCode("E12345");
        equipmentItem.setDescription("Air conditioner");
        equipmentItem.setMaintenanceType("Preventive");
        equipmentItem.setMaintenanceStatus("PENDING");

        equipmentItem.setMaintenanceTicket(maintenanceTicket);
        maintenanceTicket.setMaintenanceEquipmentItems(Collections.singletonList(equipmentItem));

        return maintenanceTicket;
    }

    private MaintenanceTicketRequest mapMaintenanceTicketToRequestDto(MaintenanceTicket maintenanceTicket) {
        MaintenanceTicketRequest requestDto = new MaintenanceTicketRequest();
        requestDto.setDescription(maintenanceTicket.getDescription());
        requestDto.setIsCompleted(maintenanceTicket.getIsCompleted());

        List<MaintenanceEquipmentItemsDto> equipmentItemsDtoList = new ArrayList<>();
        for (MaintenanceEquipmentItem equipmentItem : maintenanceTicket.getMaintenanceEquipmentItems()) {
            MaintenanceEquipmentItemsDto equipmentItemsDto = new MaintenanceEquipmentItemsDto();
            equipmentItemsDto.setEquipmentSkuCode(equipmentItem.getEquipmentSkuCode());
            equipmentItemsDto.setDescription(equipmentItem.getDescription());
            equipmentItemsDto.setMaintenanceType(equipmentItem.getMaintenanceType());
            equipmentItemsDto.setMaintenanceStatus(equipmentItem.getMaintenanceStatus());
            equipmentItemsDtoList.add(equipmentItemsDto);
        }
        requestDto.setMaintenanceEquipmentItemsList(equipmentItemsDtoList);

        return requestDto;
    }

    @Test
    void shouldCreateTicket() throws Exception {
        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/maintenance-ticket").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isCreated());

        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

    }

    @Test
    void shouldNotCreateTicketWithNullEquipments() throws Exception {
        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createMaintenanceTicket());
        request.setMaintenanceEquipmentItemsList(null);
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result =mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/maintenance-ticket").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(),ApiError.class);
        Assertions.assertEquals(400, response.getCode());
        Assertions.assertEquals("Invalid request context", response.getMessage());
        Assertions.assertEquals("Maintenance Equipment Items List is required", response.getErrorsDetails().get(0));

        Assertions.assertEquals(0, maintenanceTicketRepository.findAll().size());
    }

    @Test
    void shouldNotCreateTicketWithEmptyEquipmentLists() throws Exception {
        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createMaintenanceTicket());
        request.setMaintenanceEquipmentItemsList(Collections.emptyList());
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result =mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/maintenance-ticket").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(),ApiError.class);
        Assertions.assertEquals(400, response.getCode());
        Assertions.assertEquals("Invalid request context", response.getMessage());
        Assertions.assertEquals("Maintenance Equipment Items List is required", response.getErrorsDetails().get(0));



        Assertions.assertEquals(0, maintenanceTicketRepository.findAll().size());
    }

    @Test
    void shouldNotCreateTicketWithEmptySkuNameInEquipmentLists() throws Exception {

        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createMaintenanceTicket());

        //create new equipment item list without SKU
        List<MaintenanceEquipmentItemsDto> equipmentItemsList = new ArrayList<>();
        MaintenanceEquipmentItemsDto equipmentItem = new MaintenanceEquipmentItemsDto();
        equipmentItem.setDescription("test");
        equipmentItem.setMaintenanceStatus("PENDING");
        equipmentItem.setMaintenanceType("Preventive");
        equipmentItemsList.add(equipmentItem);


        request.setMaintenanceEquipmentItemsList(equipmentItemsList); // empty list should trow error, equipment sku is required
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result =mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/maintenance-ticket").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(),ApiError.class);
        Assertions.assertEquals(400, response.getCode());
        Assertions.assertEquals("Invalid request context", response.getMessage());
        Assertions.assertEquals("Equipment Sku code is required", response.getErrorsDetails().get(0));



        Assertions.assertEquals(0, maintenanceTicketRepository.findAll().size());
    }

    @Test
    @Transactional // keep connection open for lazy loading
    void shouldGetAllTickets() throws Exception {


        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/maintenance-ticket").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isCreated());

        MaintenanceTicket expectedTicket = maintenanceTicketRepository.findAll().get(0);


        //get the result and map to listof maintenance response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket")).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        MaintenanceTicketResponse[] maintenanceTickets = objectMapper.readValue(responseBody, MaintenanceTicketResponse[].class);

        MaintenanceTicketResponse responseTicket = maintenanceTickets[0];

        Assertions.assertEquals(expectedTicket.getTicketNumber(),responseTicket.getTicketNumber());
        Assertions.assertEquals(expectedTicket.getDescription(),responseTicket.getDescription());
        Assertions.assertEquals(expectedTicket.getId(),responseTicket.getId());
        Assertions.assertEquals(expectedTicket.getIsCompleted(),responseTicket.getIsCompleted());


        // check whether the item in the ticket match es the item in the response
        Assertions.assertEquals(expectedTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType(),responseTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType());
        Assertions.assertEquals(expectedTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus(),responseTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus());
        Assertions.assertEquals(expectedTicket.getMaintenanceEquipmentItems().get(0).getId(),responseTicket.getMaintenanceEquipmentItems().get(0).getId());
        Assertions.assertEquals(expectedTicket.getMaintenanceEquipmentItems().get(0).getDescription(),responseTicket.getMaintenanceEquipmentItems().get(0).getDescription());
        Assertions.assertEquals(expectedTicket.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode(),responseTicket.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode());




        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());
    }

    @Test
    void shouldGetTicketById() throws Exception {
        MaintenanceTicket maintenanceTicket=maintenanceTicketRepository.save(createMaintenanceTicket());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());
        Assertions.assertEquals(maintenanceTicket.getId(), response.getId());
        Assertions.assertEquals(maintenanceTicket.getTicketNumber(), response.getTicketNumber());
        Assertions.assertEquals(maintenanceTicket.getDescription(), response.getDescription());
        Assertions.assertEquals(maintenanceTicket.getIsCompleted(), response.getIsCompleted());
        // check whether the item in the ticket match es the item in the response
        Assertions.assertEquals(maintenanceTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType(),response.getMaintenanceEquipmentItems().get(0).getMaintenanceType());
        Assertions.assertEquals(maintenanceTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus(),response.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus());
        Assertions.assertEquals(maintenanceTicket.getMaintenanceEquipmentItems().get(0).getId(),response.getMaintenanceEquipmentItems().get(0).getId());
        Assertions.assertEquals(maintenanceTicket.getMaintenanceEquipmentItems().get(0).getDescription(),response.getMaintenanceEquipmentItems().get(0).getDescription());
        Assertions.assertEquals(maintenanceTicket.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode(),response.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode());
    }


    @Test
    void shouldNotGetTicketByIdNotFound() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+1)).andExpect(status().isNotFound()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(0, maintenanceTicketRepository.findAll().size());

        Assertions.assertEquals(404, response.getCode());
        Assertions.assertEquals("Not Found", response.getMessage());
        Assertions.assertEquals("Maintenance ticket with id: "+1+" not found", response.getErrorsDetails().get(0));

    }

    private MaintenanceTicket createPutMaintenanceTicket() {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();
        maintenanceTicket.setDescription("Equipment maintenance updated");
        maintenanceTicket.setIsCompleted(Boolean.TRUE);

        MaintenanceEquipmentItem equipmentItem = new MaintenanceEquipmentItem();
        equipmentItem.setEquipmentSkuCode("E12346");
        equipmentItem.setDescription("Air conditioner");
        //equipmentItem.setMaintenanceType("Preventive"); check this  to see whether this will become null
        equipmentItem.setMaintenanceStatus("FINISHED");


        equipmentItem.setMaintenanceTicket(maintenanceTicket);
        maintenanceTicket.setMaintenanceEquipmentItems(Collections.singletonList(equipmentItem));

        return maintenanceTicket;
    }
    @Test
    void shouldPutExistingTicket() throws Exception {


        MaintenanceTicket maintenanceTicket = maintenanceTicketRepository.save(createMaintenanceTicket());

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse existingTicket = objectMapper.readValue(getResult.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createPutMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/maintenance-ticket/"+existingTicket.getId()).contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isOk()).andReturn();

        MaintenanceTicketResponse actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());


        Assertions.assertEquals(existingTicket.getId(), actualTicket.getId()); // same
        Assertions.assertEquals(existingTicket.getTicketNumber(), actualTicket.getTicketNumber()); // same
        Assertions.assertNotEquals(existingTicket.getDescription(), actualTicket.getDescription());  // should be not same
        Assertions.assertNotEquals(existingTicket.getIsCompleted(), actualTicket.getIsCompleted());  // should be not same


        //equipments list validation
        Assertions.assertEquals(null, actualTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType());
        Assertions.assertEquals("FINISHED", actualTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus());
        Assertions.assertEquals("E12346", actualTicket.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode());
        Assertions.assertEquals("Air conditioner", actualTicket.getMaintenanceEquipmentItems().get(0).getDescription());
    }

    @Test
    void shouldNotPutExistingTicketNotFound() throws Exception {

        MaintenanceTicket maintenanceTicket = maintenanceTicketRepository.save(createMaintenanceTicket());

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse existingTicket = objectMapper.readValue(getResult.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createPutMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/maintenance-ticket/"+123554).contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isNotFound()).andReturn();

        ApiError actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        Assertions.assertEquals(404, actualTicket.getCode());
    }

    @Test
    void shouldNotPutExistingTicketInvalidRequest() throws Exception {
        MaintenanceTicket maintenanceTicket = maintenanceTicketRepository.save(createMaintenanceTicket());

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse existingTicket = objectMapper.readValue(getResult.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createPutMaintenanceTicket());
        request.setMaintenanceEquipmentItemsList(Collections.emptyList());
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/maintenance-ticket/"+123554).contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();

        ApiError actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        Assertions.assertEquals(400, actualTicket.getCode());
    }
    private MaintenanceTicket createPatchPartialMaintenanceTicket() {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();

        maintenanceTicket.setIsCompleted(Boolean.TRUE);

        MaintenanceEquipmentItem equipmentItem = new MaintenanceEquipmentItem();
        equipmentItem.setEquipmentSkuCode("E123456"); // beda
        equipmentItem.setDescription("Air conditioner");
        equipmentItem.setMaintenanceType("Preventive");
        equipmentItem.setMaintenanceStatus("FINISHED"); // beda
        equipmentItem.setMaintenanceTicket(maintenanceTicket);


        maintenanceTicket.setMaintenanceEquipmentItems(Collections.singletonList(equipmentItem));

        return maintenanceTicket;
    }
    @Test
    void shouldPatchExistingTicket() throws Exception {

        MaintenanceTicket maintenanceTicket = maintenanceTicketRepository.save(createMaintenanceTicket());

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse existingTicket = objectMapper.readValue(getResult.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createPatchPartialMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/maintenance-ticket/"+existingTicket.getId()).contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isOk()).andReturn();

        MaintenanceTicketResponse actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), MaintenanceTicketResponse.class);


        Assertions.assertEquals(existingTicket.getId(), actualTicket.getId()); // same
        Assertions.assertEquals(existingTicket.getTicketNumber(), actualTicket.getTicketNumber()); // same
        Assertions.assertEquals(existingTicket.getDescription(), actualTicket.getDescription());  // same
        Assertions.assertNotEquals(existingTicket.getIsCompleted(), actualTicket.getIsCompleted());  // should be not same


        //equipments list validation
        Assertions.assertNotEquals(existingTicket.getMaintenanceEquipmentItems(), actualTicket.getMaintenanceEquipmentItems());
        Assertions.assertNotEquals(existingTicket.getMaintenanceEquipmentItems().get(0).getId(), actualTicket.getMaintenanceEquipmentItems().get(0).getId()); // not same
        Assertions.assertEquals(existingTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType(), actualTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceType()); //same
        Assertions.assertEquals("FINISHED", actualTicket.getMaintenanceEquipmentItems().get(0).getMaintenanceStatus()); //different
        Assertions.assertEquals("E123456", actualTicket.getMaintenanceEquipmentItems().get(0).getEquipmentSkuCode());// same
    }

    @Test
    void shouldNotPatchExistingTicketNotFound() throws Exception {
        MaintenanceTicketRequest request = mapMaintenanceTicketToRequestDto(createPatchPartialMaintenanceTicket());
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/maintenance-ticket/"+1).contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isNotFound()).andReturn();

        ApiError actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        Assertions.assertEquals(404, actualTicket.getCode());
    }


    @Test
    void shouldDeleteExistingTicket() throws Exception {
        MaintenanceTicket maintenanceTicket = maintenanceTicketRepository.save(createMaintenanceTicket());

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/maintenance-ticket/"+maintenanceTicket.getId())).andExpect(status().isOk()).andReturn();
        MaintenanceTicketResponse existingTicket = objectMapper.readValue(getResult.getResponse().getContentAsString(), MaintenanceTicketResponse.class);
        Assertions.assertEquals(1, maintenanceTicketRepository.findAll().size());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/maintenance-ticket/"+existingTicket.getId())).andExpect(status().isOk()).andReturn();



        Assertions.assertEquals("maintenance-ticket-"+existingTicket.getId()+" successfully deleted", result.getResponse().getContentAsString());

        Assertions.assertEquals(0, maintenanceTicketRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteTicketNotFound() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/maintenance-ticket/"+1)).andExpect(status().isNotFound()).andReturn();

        ApiError actualTicket = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        Assertions.assertEquals(404, actualTicket.getCode());
    }
}

