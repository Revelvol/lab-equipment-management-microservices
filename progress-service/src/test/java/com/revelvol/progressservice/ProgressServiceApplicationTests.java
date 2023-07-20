package com.revelvol.progressservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revelvol.progressservice.dto.ApiError;
import com.revelvol.progressservice.dto.ProgressDescriptionDto;
import com.revelvol.progressservice.dto.ProgressRequest;
import com.revelvol.progressservice.model.Progress;
import com.revelvol.progressservice.model.ProgressDescription;
import com.revelvol.progressservice.repository.ProgressRepository;
import jakarta.transaction.Transactional;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProgressServiceApplicationTests {

    @Container
    final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3").withDatabaseName(
            "maintenance-service-test-db").withUsername("admin-test").withPassword("password-test");


    @DynamicPropertySource // add application properties dynamically
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private ProgressRepository progressRepository;

    @Autowired
    public ProgressServiceApplicationTests(ObjectMapper objectMapper, MockMvc mockMvc, ProgressRepository progressRepository) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.progressRepository = progressRepository;
    }

    @AfterEach
    void tearDown() {
        progressRepository.deleteAll();
    }


    private Progress createDefaultProgress() {
        Progress progress = new Progress();
        progress.setStatus("IN PROGRESS");
        progress.setSkuCode("E12345");
        progress.setProgressDescriptionList(createDefaultProgressDescriptionList());

        return progress;
    }

    private List<ProgressDescription> createDefaultProgressDescriptionList() {
        // make progress description 1
        ProgressDescription progressDescription = new ProgressDescription();
        progressDescription.setDescription("Initial machine inspection");
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();
        // Set the date to July 20, 2023
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        progressDescription.setProgressDate(calendar.getTime());


        // make progress description 2
        ProgressDescription progressDescription2 = new ProgressDescription();
        progressDescription2.setDescription("Initial machine inspection");// Set the date to July 20, 2023
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        progressDescription2.setProgressDate(calendar.getTime());

        List<ProgressDescription> progressDescriptionList = new ArrayList<>();
        progressDescriptionList.add(progressDescription);
        progressDescriptionList.add(progressDescription2);

        return progressDescriptionList;


    }

    private ProgressRequest mapProgressToRequest(Progress progress) {
        ProgressRequest progressRequest = new ProgressRequest();

        progressRequest.setStatus(progress.getStatus());
        progressRequest.setSkuCode(progress.getSkuCode());
        progressRequest.setProgressDescriptionDtoList(mapProgressDescriptionListToDto(progress.getProgressDescriptionList()));

        return progressRequest;
    }

    private List<ProgressDescriptionDto> mapProgressDescriptionListToDto(List<ProgressDescription> progressDescriptionList) {
        List<ProgressDescriptionDto> progressDescriptionDtoList = new ArrayList<>();

        for (ProgressDescription progressDescription : progressDescriptionList) {
            ProgressDescriptionDto progressDescriptionDto = new ProgressDescriptionDto();
            progressDescriptionDto.setDescription(progressDescription.getDescription());
            progressDescriptionDto.setProgressDate(progressDescription.getProgressDate());
            progressDescriptionDtoList.add(progressDescriptionDto);
        }

        return progressDescriptionDtoList;
    }

    @Test
    @Transactional
    void shouldCreateProgress() throws Exception {
        ProgressRequest progressRequest = mapProgressToRequest(createDefaultProgress());
        String jsonRequest = objectMapper.writeValueAsString(progressRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/progresses").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().isCreated()).andReturn();

        Progress progress = progressRepository.findAll().get(0);
        Assertions.assertEquals(1, progressRepository.findAll().size());
    }

    @Test
    void shouldNotCreateProgressInvalidRequestNoSkuCode() throws Exception {
        ProgressRequest progressRequest = mapProgressToRequest(createDefaultProgress());
        progressRequest.setSkuCode(null);
        String jsonRequest = objectMapper.writeValueAsString(progressRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/progresses").contentType(MediaType.APPLICATION_JSON).content(
                jsonRequest)).andExpect(status().is4xxClientError()).andReturn();

        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        Assertions.assertEquals(0, progressRepository.findAll().size());
        Assertions.assertEquals(400, response.getCode());
        Assertions.assertEquals("Invalid request context", response.getMessage());
        Assertions.assertEquals("Sku code is required", response.getErrorsDetails().get(0));

    }

    @Test
    void shouldGetAllProgress() throws Exception {
        Progress progress1 = progressRepository.save(createDefaultProgress());
        Progress progress2 = progressRepository.save(createDefaultProgress());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progresses")).andExpect(status().isOk()).andReturn();
        Progress[] response = objectMapper.readValue(result.getResponse().getContentAsString(), Progress[].class);
        Assertions.assertEquals(2, progressRepository.findAll().size());
        Assertions.assertEquals(progress1.toString(), response[0].toString());
        Assertions.assertEquals(progress2.toString(), response[1].toString());

    }

    @Test
    void shouldGetProgressById() throws Exception {
        Progress progress = progressRepository.save(createDefaultProgress());
         MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progresses/" + progress.getId())).andExpect(status().isOk()).andReturn();
        Progress response = objectMapper.readValue(result.getResponse().getContentAsString(), Progress.class);
        Assertions.assertEquals(1, progressRepository.findAll().size());
        Assertions.assertEquals(progress.getId(), response.getId());
        Assertions.assertEquals(progress.getSkuCode(), response.getSkuCode());
        Assertions.assertEquals(progress.getProgressDescriptionList().size(), response.getProgressDescriptionList().size());

        // assert the value in the list
        Assertions.assertEquals(progress.getProgressDescriptionList().get(0)
                .getDescription(), response.getProgressDescriptionList().get(0).getDescription());
        Assertions.assertEquals(progress.getProgressDescriptionList().get(0)
                .getProgressDate(), response.getProgressDescriptionList().get(0).getProgressDate());
        Assertions.assertEquals(progress.getProgressDescriptionList().get(1)
                .getDescription(), response.getProgressDescriptionList().get(1).getDescription());
        Assertions.assertEquals(progress.getProgressDescriptionList().get(1)
                .getProgressDate(), response.getProgressDescriptionList().get(1).getProgressDate());
    }

    @Test
    void shouldNotGetProgressByIdNotFound() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progresses/" + 1)).andExpect(status().isNotFound()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(0, progressRepository.findAll().size());

        Assertions.assertEquals(404, response.getCode());
        Assertions.assertEquals("Not found", response.getMessage());
        Assertions.assertEquals("Progress with id:1 not found", response.getErrorsDetails().get(0));

    }

    private Progress createUpdateProgress(){
        Progress progress = new Progress();
        progress.setStatus("FINISHED");
        progress.setSkuCode("E123456");
        progress.setProgressDescriptionList(createDefaultProgressDescriptionList());

        return progress;
    }

    @Test
    void shouldUpdateProgressById() throws Exception {
        Progress progress = progressRepository.save(createDefaultProgress());
        String jsonRequest = objectMapper.writeValueAsString(mapProgressToRequest(createUpdateProgress()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/progresses/" + progress.getId())
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk()).andReturn();

        Progress response = objectMapper.readValue(result.getResponse().getContentAsString(), Progress.class);
        Assertions.assertEquals(1, progressRepository.findAll().size());
        Assertions.assertEquals("E123456", response.getSkuCode());
        Assertions.assertEquals("FINISHED", response.getStatus());
        Assertions.assertEquals("E12345", progress.getSkuCode());
        Assertions.assertEquals(response.getWorkingOrderId(), progress.getWorkingOrderId());
        Assertions.assertEquals("IN PROGRESS", progress.getStatus());
        Assertions.assertNotEquals(progress.getProgressDescriptionList(), response.getProgressDescriptionList());



    }
    @Test
    void shouldNotUpdateProgressByIdNotFound() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(mapProgressToRequest(createUpdateProgress()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/progresses/1" )
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isNotFound()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(0, progressRepository.findAll().size());

        Assertions.assertEquals(404, response.getCode());
        Assertions.assertEquals("Not found", response.getMessage());
        Assertions.assertEquals("Progress with id:1 not found", response.getErrorsDetails().get(0));



    }

    @Test
    void shouldNotUpdateProgressInvalidRequest() throws Exception {

        Progress progress = progressRepository.save(createDefaultProgress());

        ProgressRequest request = mapProgressToRequest(createUpdateProgress());
        request.setSkuCode(null);
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/progresses/" + progress.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().is4xxClientError()).andReturn();

        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(400,response.getCode());
        Assertions.assertEquals("Invalid request context", response.getMessage());
        Assertions.assertEquals("Sku code is required", response.getErrorsDetails().get(0));

        Assertions.assertEquals(1, progressRepository.findAll().size());


    }

    @Test
    void shouldPatchProgressById() throws Exception {

        Progress progress = progressRepository.save(createDefaultProgress());
        ProgressRequest request = mapProgressToRequest(createUpdateProgress());

        request.setSkuCode(null);    // should not change the current sku
        String jsonRequest = objectMapper.writeValueAsString(request);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progresses/" + progress.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk()).andReturn();

        Progress response = objectMapper.readValue(result.getResponse().getContentAsString(), Progress.class);
        Assertions.assertEquals(1, progressRepository.findAll().size());
        Assertions.assertEquals("E12345", response.getSkuCode());
        Assertions.assertEquals(response.getWorkingOrderId(), progress.getWorkingOrderId());
        Assertions.assertEquals("FINISHED", response.getStatus());
        Assertions.assertEquals("E12345", progress.getSkuCode());
        Assertions.assertEquals("IN PROGRESS", progress.getStatus());
        Assertions.assertNotEquals(progress.getProgressDescriptionList(), response.getProgressDescriptionList());


    }

    @Test
    void shouldNotPatchProgressByIdNotFound() throws Exception {

        String jsonRequest = objectMapper.writeValueAsString(mapProgressToRequest(createUpdateProgress()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progresses/1" )
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isNotFound()).andReturn();
        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(0, progressRepository.findAll().size());

        Assertions.assertEquals(404, response.getCode());
        Assertions.assertEquals("Not found", response.getMessage());
        Assertions.assertEquals("Progress with id:1 not found", response.getErrorsDetails().get(0));

    }

    @Test
    void shouldDeleteById() throws Exception {

        Progress progress = progressRepository.save(createDefaultProgress());
        Assertions.assertEquals(1, progressRepository.findAll().size());



        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/progresses/" + progress.getId()))
                .andExpect(status().isOk()).andReturn();


        Assertions.assertEquals(0, progressRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteByIdNotFound() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/progresses/1"))
                .andExpect(status().isNotFound()).andReturn();

        ApiError response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        Assertions.assertEquals(0, progressRepository.findAll().size());
    }
}
