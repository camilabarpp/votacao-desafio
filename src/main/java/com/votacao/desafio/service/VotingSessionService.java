package com.votacao.desafio.service;

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
    private final PautaService pautaService;

    @Transactional(readOnly = true)
    public Page<VotingSession> listAllVotingSessions(Integer page, Integer size) {
        log.info("Listing all Voting Sessions");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return votingSessionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public VotingSession getVotingSessionById(Long votingSessionId) {
        return votingSessionRepository.findById(votingSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting Session not found"));
    }

    @Transactional
    public VotingSession openVotingSession(Long pautaId, Integer durationInMinutes) {
        log.info("Opening voting session for Pauta with ID: {}", pautaId);
        Pauta pauta = pautaService.getPautaById(pautaId);

        if (votingSessionRepository.existsByPautaAndVotingSessionOpenTrue(pautaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session already open for this Pauta");
        }

        return votingSessionRepository.save(VotingSession.builder()
                .pauta(pauta)
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(durationInMinutes))
                .votingSessionOpen(true)
                .build());
    }
}