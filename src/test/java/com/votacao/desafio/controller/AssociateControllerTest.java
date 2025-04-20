package com.votacao.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.votacao.desafio.dto.AssociateRequest;
import com.votacao.desafio.dto.AssociateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AssociateControllerTest")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AssociateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    private AssociateRequest associateRequest;
    private AssociateRequest updateAssociateRequest;
    private AssociateResponse updateAssociateResponse;
    private AssociateResponse savedAssociateResponse;

    @BeforeEach
    void setup(){
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        associateRequest = new AssociateRequest();
        associateRequest.setName("João Silva");
        associateRequest.setCpf("30115367080");
        associateRequest.setEmail("joao@email.com");

        updateAssociateRequest = new AssociateRequest();
        updateAssociateRequest.setName("Larissa Silva");
        updateAssociateRequest.setCpf("25231410004");
        updateAssociateRequest.setEmail("larissa@email.com");

        savedAssociateResponse = AssociateResponse.builder()
                .id(1L)
                .name("João Silva")
                .cpf("30115367080")
                .email("joao@email.com")
                .build();

        updateAssociateResponse = AssociateResponse.builder()
                .id(2L)
                .name("Larissa Silva")
                .cpf("25231410004")
                .email("larissa@email.com")
                .build();
    }

    @Test
    @DisplayName("Should create associate successfully")
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCreateAssociateSuccessfully() throws Exception {
        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(savedAssociateResponse)));
    }

    @Test
    @DisplayName("Should not create associate with duplicate Email")
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotCreateAssociateWithInvalidEmail() throws Exception {
        AssociateRequest invalidRequest = new AssociateRequest();
        invalidRequest.setName("João");
        invalidRequest.setCpf("27107610090");
        invalidRequest.setEmail("joaoemail.com");

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should not create associate with invalid CPF")
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotCreateAssociateWithInvalidCpf() throws Exception {
        AssociateRequest invalidRequest = new AssociateRequest();
        invalidRequest.setName("João");
        invalidRequest.setCpf("1234567890");
        invalidRequest.setEmail("joao@email.com");

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get associate by ID")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFindAssociateById() throws Exception {
        mockMvc.perform(get("/associados/{associateId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(savedAssociateResponse)));
    }

    @Test
    @DisplayName("Should list all associates")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFindAllAssociates() throws Exception {
        mockMvc.perform(get("/associados")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should update associate successfully")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateAssociateSuccessfully() throws Exception {

        mockMvc.perform(put("/associados/{associateId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAssociateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(updateAssociateResponse)));
    }

    @Test
    @DisplayName("Should not update associate with a duplicate Cpf")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotUpdateAssociateWithDuplicateCpf() throws Exception {
        updateAssociateRequest.setCpf("46416139073");

        mockMvc.perform(put("/associados/{associateId}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAssociateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should not update associate with a duplicate email")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotUpdateAssociateWithDuplicateEmail() throws Exception {
        updateAssociateRequest.setEmail("joao@email.com");

        mockMvc.perform(put("/associados/{associateId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAssociateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete associate successfully")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldDeleteAssociateSuccessfully() throws Exception {
        mockMvc.perform(delete("/associados/{associateId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return not found for non-existent associate")
    void shouldReturnNotFoundForNonExistentAssociate() throws Exception {
        mockMvc.perform(get("/associados/{associateId}", 999L))
                .andExpect(status().isNotFound());
    }
}