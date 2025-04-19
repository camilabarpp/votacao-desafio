package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaManagementServiceTest {

    @Mock
    private PautaQueryService pautaQueryService;

    @Mock
    private VotingSessionService votingSessionService;

    @InjectMocks
    private PautaManagementService pautaManagementService;

    private PautaRequest pautaRequest;
    private Pauta savedPauta;
    private VotingSession votingSession;
    private List<VotingSession> votingSessions;
    private Page<Pauta> pautaPage;

    @BeforeEach
    void setUp() {
        final String pautaTitle = "Test Pauta";
        final String pautaDescriptions = "Test Description";
        final LocalDateTime createdAt = LocalDateTime.now();
        final LocalDateTime endedAt = LocalDateTime.now().plusMinutes(10);

        pautaRequest = new PautaRequest();
        pautaRequest.setTitle(pautaTitle);
        pautaRequest.setDescription(pautaDescriptions);

        savedPauta = Pauta.builder()
                .id(1L)
                .title(pautaTitle)
                .description(pautaDescriptions)
                .createdAt(LocalDateTime.now())
                .build();

        votingSession = VotingSession.builder()
                .id(1L)
                .pauta(Pauta.builder()
                        .id(1L)
                        .title(pautaTitle)
                        .description(pautaDescriptions)
                        .createdAt(LocalDateTime.now())
                        .votingSession(VotingSession.builder()
                                .id(1L)
                                .votingSessionStartedAt(createdAt)
                                .votingSessionEndedAt(endedAt)
                                .votes(Collections.emptyList())
                                .build())
                        .build())
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(10))
                .votes(Collections.emptyList())
                .build();

        votingSessions = Collections.singletonList(votingSession);

        List<Pauta> pautaList = Collections.singletonList(savedPauta);
        pautaPage = new PageImpl<>(pautaList);
    }

    @Test
    @DisplayName("Should create and return paginated pauta list")
    void createPauta_ShouldCreateAndReturnPautaResponse() {
        when(pautaQueryService.save(any(Pauta.class))).thenReturn(savedPauta);

        PautaResponse result = pautaManagementService.createPauta(pautaRequest);

        assertNotNull(result);
        assertEquals(savedPauta.getId(), result.getId());
        assertEquals(savedPauta.getTitle(), result.getTitle());
        assertEquals(savedPauta.getDescription(), result.getDescription());
        verify(pautaQueryService, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Should list all pautas")
    void listAllPautas_WithNullStatus_ShouldReturnAllPautas() {
        when(pautaQueryService.findAll(any(Pageable.class))).thenReturn(pautaPage);

        Page<PautaResponse> result = pautaManagementService.listAllPautas(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(pautaQueryService, times(1)).findAll(any(Pageable.class));
        verify(votingSessionService, never()).listAllVotingSessionsOpen(anyInt(), anyInt());
        verify(votingSessionService, never()).listAllVotingSessionsClosed(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should list all pautas with status query is blank")
    void listAllPautas_WithEmptyStatus_ShouldReturnAllPautas() {
        when(pautaQueryService.findAll(any(Pageable.class))).thenReturn(pautaPage);

        Page<PautaResponse> result = pautaManagementService.listAllPautas("", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(pautaQueryService, times(1)).findAll(any(Pageable.class));
        verify(votingSessionService, never()).listAllVotingSessionsOpen(anyInt(), anyInt());
        verify(votingSessionService, never()).listAllVotingSessionsClosed(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should list all pautas with status OPEN")
    void listAllPautas_WithStatusOPEN_ShouldReturnOpenPautas() {
        Page<VotingSession> openPautaPage = new PageImpl<>(votingSessions);
        when(votingSessionService.listAllVotingSessionsOpen(0, 10)).thenReturn(openPautaPage);

        Page<PautaResponse> result = pautaManagementService.listAllPautas("OPEN", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionService, times(1)).listAllVotingSessionsOpen(0, 10);
        verify(pautaQueryService, never()).findAll(any(Pageable.class));
        verify(votingSessionService, never()).listAllVotingSessionsClosed(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should list all pautas with status CLOSED")
    void listAllPautas_WithStatusCLOSED_ShouldReturnClosedPautas() {
        Page<VotingSession> closedPautaPage = new PageImpl<>(votingSessions);
        when(votingSessionService.listAllVotingSessionsClosed(0, 10)).thenReturn(closedPautaPage);

        Page<PautaResponse> result = pautaManagementService.listAllPautas("CLOSED", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionService, times(1)).listAllVotingSessionsClosed(0, 10);
        verify(pautaQueryService, never()).findAll(any(Pageable.class));
        verify(votingSessionService, never()).listAllVotingSessionsOpen(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should list all pautas with invalid status")
    void listAllPautas_WithInvalidStatus_ShouldReturnAllPautas() {
        when(pautaQueryService.findAll(any(Pageable.class))).thenReturn(pautaPage);

        Page<PautaResponse> result = pautaManagementService.listAllPautas("INVALID_STATUS", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(pautaQueryService, times(1)).findAll(any(Pageable.class));
        verify(votingSessionService, never()).listAllVotingSessionsOpen(anyInt(), anyInt());
        verify(votingSessionService, never()).listAllVotingSessionsClosed(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should get pauta response by id")
    void getPautaResponseById_ShouldReturnPautaResponse() {
        when(pautaQueryService.getPautaById(1L)).thenReturn(savedPauta);

        PautaResponse result = pautaManagementService.getPautaResponseById(1L);

        assertNotNull(result);
        assertEquals(savedPauta.getId(), result.getId());
        assertEquals(savedPauta.getTitle(), result.getTitle());
        assertEquals(savedPauta.getDescription(), result.getDescription());
        verify(pautaQueryService, times(1)).getPautaById(1L);
    }

    @Test
    @DisplayName("Should get pauta response by id with valid id")
    void getVotingResult_WithValidPautaAndSession_ShouldReturnResult() {
        Long pautaId = 1L;
        savedPauta.setVotingSession(votingSession);
        votingSession.setVotes(List.of(Vote.builder().id(1L).associate(Associate.builder().id(1L).build()).voteOption(Vote.VoteOption.NO).build()));

        when(pautaQueryService.getPautaById(pautaId)).thenReturn(savedPauta);
        when(votingSessionService.getVotingSessionByPautaId(pautaId)).thenReturn(votingSession);

        VotingResultResponse result = pautaManagementService.getVotingResult(pautaId);

        assertNotNull(result);
        verify(pautaQueryService).getPautaById(pautaId);
        verify(votingSessionService).getVotingSessionByPautaId(pautaId);
    }

    @Test
    @DisplayName("Should throw exception when pauta is not found")
    void getVotingResult_WithPautaWithoutSession_ShouldThrowException() {
        Long pautaId = 1L;
        savedPauta.setVotingSession(null);

        when(pautaQueryService.getPautaById(pautaId)).thenReturn(savedPauta);
        when(votingSessionService.getVotingSessionByPautaId(pautaId)).thenReturn(votingSession);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> pautaManagementService.getVotingResult(pautaId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Pauta with ID " + pautaId + " does not have an voting session opened",
                exception.getReason());
        verify(pautaQueryService).getPautaById(pautaId);
        verify(votingSessionService).getVotingSessionByPautaId(pautaId);
    }
}