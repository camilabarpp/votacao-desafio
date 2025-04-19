package com.votacao.desafio.service;

import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
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
import java.util.List;
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
    private Pauta pauta;
    private VotingSession votingSession;
    private VotingSessionResponse votingSessionResponse;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "votingSessionStartedAt"));

        pauta = Pauta.builder()
                .id(1L)
                .title("Test Pauta")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .build();


        votingSession = VotingSession.builder()
                .id(1L)
                .pauta(pauta)
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(10))
                .build();

        votingSessionResponse = VotingSessionResponse.builder()
                .id(1L)
                .votingSessionStatus("OPEN")
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(10))
                .votes(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Should return a page of voting sessions")
    void getVotingSessionByPautaId_WithExistingPautaId_ShouldReturnVotingSession() {
        Long pautaId = 1L;

        when(votingSessionRepository.findByPautaId(pautaId)).thenReturn(Optional.of(votingSession));

        VotingSession result = votingSessionService.getVotingSessionByPautaId(pautaId);

        assertNotNull(result);
        assertEquals(votingSession.getId(), result.getId());
        assertEquals(votingSession.getPauta(), result.getPauta());
        assertEquals(votingSession.getVotingSessionStartedAt(), result.getVotingSessionStartedAt());
        assertEquals(votingSession.getVotingSessionEndedAt(), result.getVotingSessionEndedAt());

        verify(votingSessionRepository).findByPautaId(pautaId);
    }

    @Test
    @DisplayName("Should return voting session by pauta ID with success")
    void getVotingSessionByPautaId_WithNonExistingPautaId_ShouldThrowException() {
        Long pautaId = 999L;

        when(votingSessionRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.getVotingSessionByPautaId(pautaId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Voting Session not found for the given Pauta ID " + pautaId, exception.getReason());
        verify(votingSessionRepository).findByPautaId(pautaId);
    }

    @Test
    @DisplayName("Should return VotingSessionResponse when ID exists")
    void getVotingSessionById_WithExistingId_ShouldReturnVotingSessionResponse() {
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
    @DisplayName("Should open a new voting session for a Pauta with success")
    void openVotingSessionByPautaId_WithValidPautaAndDuration_ShouldReturnVotingSessionResponse() {
        Long pautaId = 1L;
        Integer duration = 30;
        pauta.setVotingSession(null);

        when(pautaService.getPautaById(pautaId)).thenReturn(pauta);
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);

        VotingSessionResponse result = votingSessionService.openVotingSessionByPautaId(pautaId, duration);

        assertNotNull(result);
        assertEquals(votingSessionResponse.getId(), result.getId());
        assertEquals(votingSessionResponse.getVotingSessionStatus(), result.getVotingSessionStatus());
        assertEquals(votingSessionResponse.getVotes(), result.getVotes());

        verify(pautaService).getPautaById(pautaId);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }


    @Test
    @DisplayName("Should throw exception when VotingSession already exists")
    void openVotingSessionByPautaId_WhenVotingSessionAlreadyExists_ShouldThrowException() {
        pauta.setVotingSession(new VotingSession());
        when(pautaService.getPautaById(1L)).thenReturn(pauta);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.openVotingSessionByPautaId(1L, 30)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Voting session already exists for this Pauta", exception.getReason());
        verify(pautaService, times(1)).getPautaById(1L);
        verifyNoInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should throw exception when VotingSession already exists")
    void openVotingSessionByPautaId_WithExistingVotingSession_ShouldThrowException() {
        pauta.setVotingSession(new VotingSession());
        when(pautaService.getPautaById(1L)).thenReturn(pauta);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.openVotingSessionByPautaId(1L, 30)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
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
    @DisplayName("Should save and return VotingSession")
    void saveVotingSession_WithValidVotingSession_ShouldSaveAndReturn() {
        when(votingSessionRepository.save(votingSession)).thenReturn(votingSession);

        VotingSession result = votingSessionService.saveVotingSession(votingSession);

        assertNotNull(result);
        assertEquals(votingSession, result);
        verify(votingSessionRepository, times(1)).save(votingSession);
    }

    @Test
    @DisplayName("Should return VotingSession when ID exists")
    void findById_WithExistingId_ShouldReturnVotingSession() {
        votingSession.setId(1L);
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));

        VotingSession result = votingSessionService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(votingSessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when VotingSession not found")
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
    @DisplayName("Should list all voting sessions open with success")
    void listAllVotingSessionsOpen_WithValidPageAndSize_ShouldReturnOpenSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSession> result = votingSessionService.listAllVotingSessionsOpen(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsOpen(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Should list all voting sessions closed with success")
    void listAllVotingSessionsClosed_WithValidPageAndSize_ShouldReturnClosedSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Collections.singletonList(new VotingSession()));
        when(votingSessionRepository.listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable))).thenReturn(votingSessionPage);

        Page<VotingSession> result = votingSessionService.listAllVotingSessionsClosed(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(votingSessionRepository, times(1)).listAllVotingSessionsClosed(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Should save and return VotingSession when opening a new session")
    void openVotingSession_WithValidPautaAndDuration_ShouldSaveAndReturnVotingSession() {
        pauta.setId(1L);
        int sessionDuration = 30;

        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);

        VotingSession result = votingSessionService.openVotingSession(pauta, sessionDuration);

        assertNotNull(result);
        assertEquals(pauta, result.getPauta());
        verify(votingSessionRepository, times(1)).save(any(VotingSession.class));
    }

    @Test
    @DisplayName("Should update and return VotingSession when updating session")
    void updateVotingSession_WithValidSessionAndDuration_ShouldUpdateAndReturnResponse() {
        // given
        Long sessionId = 1L;
        int additionalMinutes = 30;
        LocalDateTime initialEndTime = LocalDateTime.now().plusMinutes(10);

        VotingSession updatedSession = VotingSession.builder()
                .id(sessionId)
                .votingSessionEndedAt(initialEndTime.plusMinutes(additionalMinutes))
                .build();

        VotingSessionResponse expectedResponse = VotingSessionResponse.builder()
                .id(sessionId)
                .votingSessionEndedAt(initialEndTime.plusMinutes(additionalMinutes))
                .build();

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(votingSession));
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(updatedSession);

        VotingSessionResponse result = votingSessionService.updateVotingSession(sessionId, additionalMinutes);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getVotingSessionEndedAt(), result.getVotingSessionEndedAt());

        verify(votingSessionRepository).findById(sessionId);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to update a closed session")
    void updateVotingSession_WithClosedSession_ShouldThrowException() {
        Long sessionId = 1L;
        int additionalMinutes = 30;

        votingSession.setVotingSessionStartedAt(LocalDateTime.now().minusMinutes(additionalMinutes));
        votingSession.setVotingSessionEndedAt(LocalDateTime.now().minusMinutes(2));

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(votingSession));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.updateVotingSession(sessionId, additionalMinutes)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Cannot update a closed session", exception.getReason());

        verify(votingSessionRepository).findById(sessionId);
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should throw exception when trying to update a session with existing votes")
    void updateVotingSession_WithExistingVotes_ShouldThrowException() {
        Long sessionId = 1L;
        Integer additionalMinutes = 30;
        votingSession.setVotes(
                List.of(
                        Vote.builder()
                                .id(1L)
                                .votingSession(votingSession)
                                .build())
        );

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(votingSession));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.updateVotingSession(sessionId, additionalMinutes)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Cannot update a session that already has votes", exception.getReason());

        verify(votingSessionRepository).findById(sessionId);
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should throw exception when trying to update a non-existing session")
    void updateVotingSession_WithNonExistingSession_ShouldThrowException() {
        Long sessionId = 999L;
        Integer additionalMinutes = 30;

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.updateVotingSession(sessionId, additionalMinutes)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Voting Session not found", exception.getReason());

        verify(votingSessionRepository).findById(sessionId);
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should close voting session and return response")
    void closeVotingSession_WithOpenSession_ShouldCloseAndReturnResponse() {
        Long sessionId = 1L;

        VotingSession closedSession = VotingSession.builder()
                .id(sessionId)
                .votingSessionEndedAt(LocalDateTime.now())
                .build();

        VotingSessionResponse expectedResponse = VotingSessionResponse.builder()
                .id(sessionId)
                .votingSessionEndedAt(closedSession.getVotingSessionEndedAt())
                .build();

        // when
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(votingSession));
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(closedSession);

        // then
        VotingSessionResponse result = votingSessionService.closeVotingSession(sessionId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals("CLOSED", result.getVotingSessionStatus());

        verify(votingSessionRepository).findById(sessionId);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to close an already closed session")
    void closeVotingSession_WithClosedSession_ShouldThrowException() {
        Long sessionId = 1L;
        votingSession.setVotingSessionEndedAt(LocalDateTime.now().minusMinutes(1));

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(votingSession));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.closeVotingSession(sessionId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Cannot close an already closed session", exception.getReason());

        verify(votingSessionRepository).findById(sessionId);
        verifyNoMoreInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Should throw exception when trying to close a non-existing session")
    void closeVotingSession_WithNonExistingSession_ShouldThrowException() {
        Long sessionId = 999L;

        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                votingSessionService.closeVotingSession(sessionId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Voting Session not found", exception.getReason());

        verify(votingSessionRepository).findById(sessionId);
        verifyNoMoreInteractions(votingSessionRepository);
    }
}