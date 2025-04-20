package com.votacao.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PautaControllerTest")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/test-scripts/setup-pautas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/pautas";

    private ObjectMapper objectMapper;

    private PautaRequest pautaRequest;
    private PautaResponse pautaResponse;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        pautaRequest = new PautaRequest();
        pautaRequest.setTitle("Pauta de Teste");
        pautaRequest.setDescription("Descrição da pauta de teste");

        pautaResponse = PautaResponse.builder()
                .id(1L)
                .title("Pauta de Teste")
                .description("Descrição da pauta de teste")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create a pauta successfully")
    void shouldCreatePautaSuccessfully() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pautaRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(pautaResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(pautaResponse.getDescription()));
    }

    @Test
    @DisplayName("Should get all pautas successfully")
    void shouldGetPautaById() throws Exception {
        Long pautaId = 1L;

        mockMvc.perform(get(BASE_URL + "/{pautaId}", pautaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pautaId));
    }

    @Test
    @DisplayName("Should return not found for invalid pauta id")
    void shouldReturnNotFoundForInvalidPautaId() throws Exception {
        Long invalidPautaId = 999L;

        mockMvc.perform(get(BASE_URL + "/{pautaId}", invalidPautaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should list all pautas paginated successfully")
    void shouldListAllPautas() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    @DisplayName("Should list pautas by status")
    void shouldListPautasByStatus() throws Exception {
        String status = "CREATED";

        mockMvc.perform(get(BASE_URL)
                        .param("status", status)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[*].status", everyItem(is(status))));
    }

    @Test
    @DisplayName("Should get voting session result successfully")
    @Sql(scripts = "/test-scripts/setup-associados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/setup-pautas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/setup-sessao-votacao.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/setup-votos.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGetVotingResult() throws Exception {
        Long pautaId = 1L;

        mockMvc.perform(get(BASE_URL + "/{pautaId}/resultado", pautaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pautaId))
                .andExpect(jsonPath("$.votingResult.votesCount").exists())
                .andExpect(jsonPath("$.votingResult.votesCountYes").exists())
                .andExpect(jsonPath("$.votingResult.votesCountNo").exists())
                .andExpect(jsonPath("$.votingResult.percentageYes").exists())
                .andExpect(jsonPath("$.votingResult.percentageNo").isNumber())
                .andExpect(jsonPath("$.votingResult.result").exists());
    }
}