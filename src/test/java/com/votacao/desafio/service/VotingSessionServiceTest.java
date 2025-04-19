package com.votacao.desafio.service;

import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private PautaQueryService pautaService;

    @InjectMocks
    private VotingSessionService votingSessionService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "votingSessionStartedAt"));
    }

    @Test
    @DisplayName("Deve retornar a sessão de votação pelo ID com sucesso")
    void getVotingSessionById_WithExistingId_ShouldReturnVotingSessionResponse() {
        VotingSession votingSession = new VotingSession();
        votingSession.setId(1L);
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));

        VotingSessionResponse result = votingSessionService.getVotingSessionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(votingSessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when Pauta not found")
    void openVotingSessionByPautaId_WithInvalidPautaId_ShouldThrowException() {
        when(pautaService.getPautaById(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.openVotingSessionByPautaId(1L, 30)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Pauta not found", exception.getReason());
        verify(pautaService, times(1)).getPautaById(1L);
        verifyNoInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe uma sessão de votação para a pauta")
    void openVotingSessionByPautaId_WhenVotingSessionAlreadyExists_ShouldThrowException() {
        // Arrange
        Pauta pauta = new Pauta();
        pauta.setVotingSession(new VotingSession()); // Simula uma pauta com sessão de votação existente
        when(pautaService.getPautaById(1L)).thenReturn(pauta);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.openVotingSessionByPautaId(1L, 30)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Voting session already exists for this Pauta", exception.getReason());
        verify(pautaService, times(1)).getPautaById(1L);
        verifyNoInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should open voting session successfully")
    void openVotingSessionByPautaId_WithExistingVotingSession_ShouldThrowException() {
        Pauta pauta = new Pauta();
        pauta.setVotingSession(new VotingSession());
        when(pautaService.getPautaById(1L)).thenReturn(pauta);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.openVotingSessionByPautaId(1L, 30)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Voting session already exists for this Pauta", exception.getReason());
        verify(pautaService, times(1)).getPautaById(1L);
        verifyNoInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should return all voting sessions with status is invalid")
    void listAllVotingSessions_WithInvalidStatus_ShouldReturnAllSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.findAll(pageable)).thenReturn(votingSessionPage);

        Page<VotingSessionResponse> result = votingSessionService.listAllVotingSessions("INVALID", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should return all voting sessions with status OPEN")
    void listAllVotingSessions_WithOpenStatus_ShouldReturnOpenSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSessionResponse> result = votingSessionService.listAllVotingSessions("OPEN", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should return all voting sessions with status CLOSED")
    void listAllVotingSessions_WithClosedStatus_ShouldReturnClosedSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSessionResponse> result = votingSessionService.listAllVotingSessions("CLOSED", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Deve salvar uma sessão de votação com sucesso")
    void saveVotingSession_WithValidVotingSession_ShouldSaveAndReturn() {
        VotingSession votingSession = new VotingSession();
        when(votingSessionRepository.save(votingSession)).thenReturn(votingSession);

        VotingSession result = votingSessionService.saveVotingSession(votingSession);

        assertNotNull(result);
        assertEquals(votingSession, result);
        verify(votingSessionRepository, times(1)).save(votingSession);
    }

    @Test
    @DisplayName("Deve retornar uma sessão de votação pelo ID")
    void findById_WithExistingId_ShouldReturnVotingSession() {
        VotingSession votingSession = new VotingSession();
        votingSession.setId(1L);
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));

        VotingSession result = votingSessionService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(votingSessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar sessão de votação com ID inexistente")
    void findById_WithNonExistingId_ShouldThrowException() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.findById(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Voting Session not found", exception.getReason());
        verify(votingSessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve listar todas as sessões de votação abertas com sucesso")
    void listAllVotingSessionsOpen_WithValidPageAndSize_ShouldReturnOpenSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSession> result = votingSessionService.listAllVotingSessionsOpen(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve listar todas as sessões de votação fechadas com sucesso")
    void listAllVotingSessionsClosed_WithValidPageAndSize_ShouldReturnClosedSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSession> result = votingSessionService.listAllVotingSessionsClosed(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve abrir uma nova sessão de votação com sucesso")
    void openVotingSession_WithValidPautaAndDuration_ShouldSaveAndReturnVotingSession() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        int sessionDuration = 30;

        VotingSession votingSession = VotingSession.builder()
                .pauta(pauta)
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(sessionDuration))
                .build();

        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);

        VotingSession result = votingSessionService.openVotingSession(pauta, sessionDuration);

        assertNotNull(result);
        assertEquals(pauta, result.getPauta());
        verify(votingSessionRepository, times(1)).save(any(VotingSession.class));
    }
}