package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;

    //    private final PautaService pautaService;
//    private final AssociateService associateService;
//    private final VoteRepository voteRepository;
//
//    @Transactional(readOnly = true)
//    public Page<VotingSession> listAllVotingSessionsOpen(Integer page, Integer size) {
//        log.info("Listing all Voting Sessions");
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return votingSessionRepository.listAllVotingSessionsOpen(pageable);
//    }
//

    @Transactional(readOnly = true)
    public VotingSession getVotingSessionById(Long votingSessionId) {
        return findById(votingSessionId);
    }

    @Transactional
    public VotingSession saveVotingSession(VotingSession votingSession) {
        return votingSessionRepository.save(votingSession);
    }

    @Transactional
    public VotingSession findOpenVotingSessionByPautaId(Long pautaId) {
        return votingSessionRepository.findOpenVotingSessionByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting Session not found for the given Pauta ID" + pautaId));
    }

    private VotingSession findById(Long votingSessionId) {
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
}