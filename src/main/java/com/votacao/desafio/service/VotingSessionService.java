package com.votacao.desafio.service;

import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.votacao.desafio.dto.VotingResultResponse.buildVotingResultResponse;
import static com.votacao.desafio.dto.VotingSessionResponse.mapToVotingSessionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final PautaQueryService pautaService;

    @Transactional(readOnly = true)
    public VotingSessionResponse getVotingSessionById(Long votingSessionId) {
        VotingSession currentVotingSession = findById(votingSessionId);
        return mapToVotingSessionResponse(currentVotingSession);
    }

    public VotingSessionResponse openVotingSessionByPautaId(Long pautaId, Integer votingSessionDurationInMinutes) {
        log.info("Opening voting session for Pauta with ID: {}", pautaId);
        Pauta pauta = pautaService.getPautaById(pautaId);

        if (pauta.getVotingSession() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session already exists for this Pauta");
        }

        VotingSession votingSession = openVotingSession(pauta, votingSessionDurationInMinutes);
        return mapToVotingSessionResponse(votingSession);
    }

    public VotingResultResponse getVotingResult(Long pautaId) {
        Pauta pauta = pautaService.getPautaById(pautaId);

        VotingSession votingSession = votingSessionRepository.findOpenVotingSessionByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting Session not found for the given Pauta ID" + pautaId));

        if (pauta.getVotingSession() == null) {
            log.error("Pauta with ID {} does not have an voting session opened", pautaId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pauta with ID " + pautaId + " does not have an voting session opened");
        }

        return buildVotingResultResponse(pauta, votingSession);
    }

    @Transactional
    public VotingSession saveVotingSession(VotingSession votingSession) {
        return votingSessionRepository.save(votingSession);
    }

    public VotingSession findById(Long votingSessionId) {
        return votingSessionRepository.findById(votingSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting Session not found"));
    }

    @Transactional(readOnly = true)
    public Page<VotingSession> listAllVotingSessionsOpen(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "votingSessionStartedAt"));
        return votingSessionRepository.listAllVotingSessionsOpen(LocalDateTime.now(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<VotingSession> listAllVotingSessionsClosed(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "votingSessionStartedAt"));
        return votingSessionRepository.listAllVotingSessionsClosed(LocalDateTime.now(), pageable);
    }

    @Transactional
    public VotingSession openVotingSession(Pauta pauta, Integer sessionDuration) {
        log.info("Opening voting session for Pauta with ID: {}", pauta.getId());
        return votingSessionRepository.save(VotingSession.builder()
                .pauta(pauta)
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(sessionDuration))
                .build());
    }

    public Page<VotingSessionResponse> listAllVotingSessions(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "votingSessionStartedAt"));
        Page<VotingSession> votingSessions;

        if ("OPEN".equalsIgnoreCase(status)) {
            votingSessions = votingSessionRepository.listAllVotingSessionsOpen(LocalDateTime.now(), pageable);
        } else if ("CLOSED".equalsIgnoreCase(status)) {
            votingSessions = votingSessionRepository.listAllVotingSessionsClosed(LocalDateTime.now(), pageable);
        } else {
            votingSessions = votingSessionRepository.findAll(pageable);
        }

        return votingSessions.map(VotingSessionResponse::mapToVotingSessionResponse);
    }
}