package com.votacao.desafio.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("VotingSessionControllerTest")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
        "/test-scripts/cleanup.sql",
        "/test-scripts/setup-pautas.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-scripts/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/sessoes/";
    private Integer votingSessionDuration;

    @BeforeEach
    void setup() {
        votingSessionDuration = 30;
    }

    @Test
    @DisplayName("Should create a voting session successfully")
    void shouldCreateVotingSessionSuccessfully() throws Exception {
        mockMvc.perform(post(BASE_URL + "pauta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should create a voting session with default duration")
    void shouldCreateVotingSessionWithDefaultDuration() throws Exception {
        mockMvc.perform(post(BASE_URL + "pauta/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.votingSessionStatus").value("OPEN"));
    }

    @Test
    @DisplayName("Should throw exception when trying to create voting session for non-existent pauta")
    void shouldThrowExceptionWhenTryingToCreateVotingSessionForNonExistentPauta() throws Exception {
        mockMvc.perform(post(BASE_URL + "pauta/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Pauta not found with ID: 999"));
    }

    @Test
    @DisplayName("Should throw exception when trying to create voting session with negative duration")
    void shouldThrowExceptionWhenTryingToCreateVotingSessionWithNegativeDuration() throws Exception {
        votingSessionDuration = -5;

        mockMvc.perform(post(BASE_URL + "pauta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting session duration must be at least 1 minute"));
    }

    @Test
    @DisplayName("Should throw exception when trying to create voting session for pauta that already has a session")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenTryingToCreateVotingSessionForPautaWithExistingSession() throws Exception {
        mockMvc.perform(post(BASE_URL + "pauta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting session already exists for this Pauta"));
    }

    @Test
    @DisplayName("Should get voting session by ID successfully")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGetVotingSessionByIdSuccessfully() throws Exception {
        mockMvc.perform(get(BASE_URL + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should throw exception when trying to get voting session by non-existent ID")
    void shouldThrowExceptionWhenTryingToGetVotingSessionByNonExistentId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting Session not found"));
    }

    @Test
    @DisplayName("Should update voting session successfully")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateVotingSessionSuccessfully() throws Exception {
        votingSessionDuration = 60;
        mockMvc.perform(put(BASE_URL + "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.votingSessionStartedAt").exists())
                .andExpect(jsonPath("$.votingSessionEndedAt").exists());
    }

    @Test
    @DisplayName("Should not update a closed voting session")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotUpdateClosedVotingSession() throws Exception {
        votingSessionDuration = 60;
        mockMvc.perform(put(BASE_URL + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cannot update a closed session"));
    }

    @Test
    @DisplayName("Should not update a voting session when it has votes")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql",
            "/test-scripts/setup-votos.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotUpdateVotingSessionWhenItHasVotes() throws Exception {
        votingSessionDuration = 60;
        mockMvc.perform(put(BASE_URL + "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cannot update a session that already has votes"));
    }

    @Test
    @DisplayName("Should throw exception when trying to update voting session with invalid duration")
    void shouldThrowExceptionWhenTryingToUpdateVotingSessionWithInvalidDuration() throws Exception {
        votingSessionDuration = -1;
        mockMvc.perform(put(BASE_URL + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting session duration must be at least 1 minute"));
    }

    @Test
    @DisplayName("Should throw exception when trying to update voting session with invalid ID")
    void shouldThrowExceptionWhenTryingToUpdateVotingSessionWithInvalidId() throws Exception {
        mockMvc.perform(put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("votingSessionDurationInMinutes", String.valueOf(votingSessionDuration)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting Session not found"));
    }

    @Test
    @DisplayName("Should close voting session successfully")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCloseVotingSessionSuccessfully() throws Exception {
        mockMvc.perform(patch(BASE_URL + "4/fechar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.votingSessionStatus").value("CLOSED"));
    }

    @Test
    @DisplayName("Should not close voting session if it's already closed")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotCloseVotingSessionIfItIsAlreadyClosed() throws Exception {
        mockMvc.perform(patch(BASE_URL + "1/fechar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cannot close an already closed session"));
    }


    @Test
    @DisplayName("Should throw exception when trying to close voting session with invalid ID")
    void shouldThrowExceptionWhenTryingToCloseVotingSessionWithInvalidId() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/999/fechar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting Session not found"));
    }

    @Test
    @DisplayName("Should list all voting sessions")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldListAllVotingSessions() throws Exception {
        mockMvc.perform(get("/sessoes?votingSessionStatus=")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    @DisplayName("Should list all voting sessions by OPEN status")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldListAllVotingSessionsByOpenStatus() throws Exception {
        mockMvc.perform(get("/sessoes?votingSessionStatus=OPEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content.[0].votingSessionStatus").value("OPEN"));
    }

    @Test
    @DisplayName("Should list all voting sessions by CLOSED status")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldListAllVotingSessionsByClosedStatus() throws Exception {
        mockMvc.perform(get("/sessoes?votingSessionStatus=CLOSED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content.[0].votingSessionStatus").value("CLOSED"));
    }

    @Test
    @DisplayName("Should delete a voting session")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldDeleteVotingSession() throws Exception {
        mockMvc.perform(delete("/sessoes/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should throw exception when trying to delete a voting session with invalid ID")
    void shouldThrowExceptionWhenTryingToDeleteVotingSessionWithInvalidId() throws Exception {
        mockMvc.perform(delete("/sessoes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting Session not found"));
    }

    @Test
    @DisplayName("Should throw exception when trying to delete a voting session with votes")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql",
            "/test-scripts/setup-votos.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenTryingToDeleteVotingSessionWithVotes() throws Exception {
        mockMvc.perform(delete("/sessoes/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cannot delete a session that has votes"));
    }

    @Test
    @DisplayName("Should throw exception when trying to delete a closed voting session")
    @Sql(scripts = {
            "/test-scripts/cleanup.sql",
            "/test-scripts/setup-pautas.sql",
            "/test-scripts/setup-associados.sql",
            "/test-scripts/setup-sessao-votacao.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-scripts/cleanup.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenTryingToDeleteClosedVotingSession() throws Exception {
        mockMvc.perform(delete("/sessoes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cannot delete a closed session"));
    }
}