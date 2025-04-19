package com.votacao.desafio.service;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private PautaQueryService pautaService;

    @Mock
    private VotingSessionService votingSessionService;

    @Mock
    private AssociateService associateService;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CpfValidationService cpfValidationService;

    @InjectMocks
    private VoteService voteService;

    private Pauta pauta;
    private VotingSession votingSession;
    private Associate associate;
    private VoteRequest voteRequest;
    private String cpf;

    @BeforeEach
    void setUp() {
        pauta = new Pauta();
        pauta.setId(1L);
        cpf = "06734690008";

        votingSession = new VotingSession();
        votingSession.setId(1L);
        votingSession.setPauta(pauta);
        votingSession.setVotingSessionStartedAt(LocalDateTime.now().minusMinutes(5));
        votingSession.setVotingSessionEndedAt(LocalDateTime.now().plusMinutes(10));
        votingSession.setVotes(new ArrayList<>());

        pauta.setVotingSession(votingSession);

        associate = new Associate();
        associate.setId(1L);

        voteRequest = new VoteRequest();
        voteRequest.setCpf(cpf);
        voteRequest.setVote(Vote.VoteOption.NO);
    }

    @Test
    @DisplayName("Should register vote successfully")
    void registerVote_WithValidData_ShouldRegisterVote() {
        when(pautaService.getPautaById(1L)).thenReturn(pauta);
        when(votingSessionService.findById(1L)).thenReturn(votingSession);
        when(cpfValidationService.validateCpf(cpf)).thenReturn(CpfValidationService.StatusVotacao.ABLE_TO_VOTE);
        when(associateService.getAssociateByCpf(cpf)).thenReturn(associate);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VotingResultResponse result = voteService.registerVote(1L, voteRequest);

        assertNotNull(result);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(votingSessionService, times(1)).saveVotingSession(any(VotingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when Pauta has no voting session opened")
    void registerVote_WhenPautaHasNoVotingSession_ShouldThrowException() {
        when(pautaService.getPautaById(1L)).thenReturn(pauta);
        pauta.setVotingSession(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                voteService.registerVote(1L, voteRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Pauta with ID 1 does not have an voting session opened", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when CPF is invalid")
    void registerVote_WithInvalidCpf_ShouldThrowException() {
        pauta.setVotingSession(votingSession);
        when(pautaService.getPautaById(1L)).thenReturn(pauta);
        when(votingSessionService.findById(1L)).thenReturn(votingSession);
        when(cpfValidationService.validateCpf(cpf)).thenReturn(CpfValidationService.StatusVotacao.UNABLE_TO_VOTE);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                voteService.registerVote(1L, voteRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid CPF " + cpf, exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when voting session was ended")
    void registerVote_WhenVotingSessionIsClosed_ShouldThrowException() {
        pauta.setVotingSession(votingSession);
        votingSession.setVotingSessionEndedAt(LocalDateTime.now().minusMinutes(1));
        when(pautaService.getPautaById(1L)).thenReturn(pauta);
        when(votingSessionService.findById(1L)).thenReturn(votingSession);
        when(cpfValidationService.validateCpf(cpf)).thenReturn(CpfValidationService.StatusVotacao.ABLE_TO_VOTE);
        when(associateService.getAssociateByCpf(cpf)).thenReturn(associate);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                voteService.registerVote(1L, voteRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Voting session is closed", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when associate already voted")
    void registerVote_WhenAssociateAlreadyVoted_ShouldThrowException() {
        pauta.setVotingSession(votingSession);
        votingSession.setVotes(Collections.singletonList(Vote.builder().associate(associate).build()));
        when(pautaService.getPautaById(1L)).thenReturn(pauta);
        when(votingSessionService.findById(1L)).thenReturn(votingSession);
        when(cpfValidationService.validateCpf(cpf)).thenReturn(CpfValidationService.StatusVotacao.ABLE_TO_VOTE);
        when(associateService.getAssociateByCpf(cpf)).thenReturn(associate);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                voteService.registerVote(1L, voteRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Associate already voted", exception.getReason());
    }
}