package com.votacao.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.dto.VotingSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.votacao.desafio.dto.VoteResponse.builder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("VoteControllerTest")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = {
                "/test-scripts/cleanup.sql",
                "/test-scripts/setup-associados.sql",
                "/test-scripts/setup-pautas.sql",
                "/test-scripts/setup-sessao-votacao.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/test-scripts/cleanup.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/votos/pauta";
    private ObjectMapper objectMapper;
    private VoteRequest voteRequest;
    private VotingResultResponse votingResultResponse;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        voteRequest = new VoteRequest();
        voteRequest.setCpf("55151007009");
        voteRequest.setVote("YES");

        VotingSessionResponse votingSessionResponse = new VotingSessionResponse();
        votingSessionResponse.setId(1L);
        votingSessionResponse.setVotes(List.of(builder().associateId(1L).votedOption("YES").build()));

        votingResultResponse = new VotingResultResponse();
        votingResultResponse.setVotingSession(votingSessionResponse);
    }


    //Poderá falhar porque o service de validar cpf retorna o status aleatoriamente
    @Test
    @DisplayName("Should register a vote successfully")
    void shouldRegisterVoteSuccessfully() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.votingSession.votes").isNotEmpty());
    }

    @Test
    @DisplayName("Should throw an exception when trying to register a vote for an invalid cpf")
    void shouldThrowExceptionWhenTryingToRegisterVoteForInvalidCpf() throws Exception {
        voteRequest.setCpf("12345678901");
        mockMvc.perform(post(BASE_URL + "/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("CPF is invalid"));
    }

    //Poderá falhar porque o service de validar cpf retorna o status aleatoriamente
    @Test
    @DisplayName("Should throw an exception when trying to register a vote and the voting session does not open")
    void shouldThrowExceptionWhenTryingToRegisterVoteAndTheVotingSessionDoesNotOpen() throws Exception {
        mockMvc.perform(post(BASE_URL + "/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Pauta with ID 3 does not have an voting session opened"));
    }

    //Poderá falhar porque o service de validar cpf retorna o status aleatoriamente
    @Test
    @DisplayName("Should throw an exception when trying to register a vote and the voting session is closed")
    void shouldThrowExceptionWhenTryingToRegisterVoteAndTheVotingSessionIsClosed() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Voting session is closed"));
    }

    @Test
    @DisplayName("Should throw an exception when trying to register a vote with an invalid vote option")
    void shouldThrowExceptionWhenTryingToRegisterVoteWithInvalidVoteOption() throws Exception {
        voteRequest.setVote("INVALID");
        mockMvc.perform(post(BASE_URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid vote option: INVALID. Valid options are: YES, NO"));
    }
}