package com.votacao.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.votacao.desafio.dto.*;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PautaControllerTest")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
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

        VoteResponse voteResponse1 = new VoteResponse();
        voteResponse1.setId(1L);
        voteResponse1.setAssociateName("João Silva");
        voteResponse1.setVotedOption(Vote.VoteOption.NO.name());

        VoteResponse voteResponse2 = new VoteResponse();
        voteResponse2.setId(2L);
        voteResponse2.setAssociateName("Maria Santos");
        voteResponse2.setVotedOption(Vote.VoteOption.YES.name());

        VoteResponse voteResponse3 = new VoteResponse();
        voteResponse3.setId(3L);
        voteResponse3.setAssociateName("Pedro Oliveira");
        voteResponse3.setVotedOption(Vote.VoteOption.NO.name());

        VotingSessionResponse votingSessionResponse = new VotingSessionResponse();
        votingSessionResponse.setId(1L);
        votingSessionResponse.setVotingSessionStartedAt(LocalDateTime.now());
        votingSessionResponse.setVotingSessionEndedAt(LocalDateTime.now().plusMinutes(10));
        votingSessionResponse.setVotingSessionStatus("OPEN");
        votingSessionResponse.setVotes(List.of(voteResponse1, voteResponse2, voteResponse3));

        pautaResponse.setVotingSession(votingSessionResponse);
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso")
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @DisplayName("Deve retornar uma pauta por ID")
    @Sql(scripts = "/test-scripts/setup-pautas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGetPautaById() throws Exception {
        Long pautaId = 1L;

        mockMvc.perform(get(BASE_URL + "/{pautaId}", pautaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pautaId));
    }

    @Test
    @DisplayName("Deve retornar erro 404 ao buscar pauta inexistente")
    void shouldReturnNotFoundForInvalidPautaId() throws Exception {
        Long invalidPautaId = 999L;

        mockMvc.perform(get(BASE_URL + "/{pautaId}", invalidPautaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todas as pautas paginadas")
    @Sql(scripts = "/test-scripts/setup-pautas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @DisplayName("Deve listar pautas filtradas por status")
    @Sql(scripts = "/test-scripts/setup-pautas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @DisplayName("Deve retornar o resultado da votação de uma pauta")
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