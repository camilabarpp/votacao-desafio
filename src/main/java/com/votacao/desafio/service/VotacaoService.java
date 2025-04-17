package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.PautaRepository;
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
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final VotingSessionRepository votingSessionRepository;

    @Transactional
    public Pauta createPauta(PautaRequest pautaRequest) {
        log.info("Creating new Pauta with title: {}", pautaRequest.getTitle());
        return pautaRepository.save(Pauta.builder()
                .title(pautaRequest.getTitle())
                .description(pautaRequest.getDescription())
                .build());
    }

    @Transactional(readOnly = true)
    public Page<Pauta> listAllPautas(Integer page, Integer size) {
        log.info("Listing all Pautas");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return pautaRepository.findAll(pageable);
    }

    @Transactional
    public VotingSession openVotingSession(Long pautaId, Integer durationInMinutes) {
        log.info("Opening voting session for Pauta with ID: {}", pautaId);
        Pauta pauta = getPautaById(pautaId);
        return votingSessionRepository.save(VotingSession.builder()
                .pauta(pauta)
                .votingSessionStartedAt(LocalDateTime.now())
                .votingSessionEndedAt(LocalDateTime.now().plusMinutes(durationInMinutes))
                .votingSessionOpen(true)
                .build());
    }

    @Transactional
    public void registerVote(Long pautaId, VoteRequest voteRequest) {
        log.info("Registering vote for Pauta with ID: {}", pautaId);
        Pauta pauta = getPautaById(pautaId);
        VotingSession votingSession = getSessaoVotacao(pauta.getId());

        if (!votingSession.isVotingSessionOpen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session is closed");
        }

        if (votingSession.getVotingSessionEndedAt().isBefore(LocalDateTime.now())) {
            votingSession.setVotingSessionOpen(false);
            votingSessionRepository.save(votingSession);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session has ended");
        }


    }

    private VotingSession getSessaoVotacao(Long pautaId) {
        return votingSessionRepository.findByPauta_Id(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting session not found"));
    }

    private Pauta getPautaById(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found"));
    }

}
