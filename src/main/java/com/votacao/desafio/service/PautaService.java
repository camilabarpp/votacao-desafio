package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.AssociateRepository;
import com.votacao.desafio.repository.PautaRepository;
import com.votacao.desafio.repository.VoteRepository;
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
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final AssociateRepository associateRepository;
    private final VoteRepository voteRepository;

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

    public Pauta getPautaById(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found"));
    }

    @Transactional
    public void registerVote(Long pautaId, VoteRequest voteRequest) {
        log.info("Registering vote for Pauta with ID: {}", pautaId);
        Pauta pauta = getPautaById(pautaId);
        VotingSession votingSession = getSessaoVotacao(pauta.getId());
        Associate associate = associateRepository.findById(voteRequest.getAssociatedId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associate not found"));

        boolean associateVoted = votingSession.getVotes().stream().anyMatch(vote -> vote.getAssociateId().equals(associate.getId()));
        if (associateVoted) {
            log.error("Associate {} already voted", associate.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate already voted");
        }

        if (!votingSession.isVotingSessionOpen()) {
            log.error("Voting session {} open", votingSession.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session is closed");
        }

        if (votingSession.getVotingSessionEndedAt().isBefore(LocalDateTime.now())) {
            votingSessionRepository.save(votingSession);
            log.error("Voting session {} has ended", votingSession.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session has ended");
        }

        Vote vote = Vote.builder()
                .votingSession(votingSession)
                .associateId(associate.getId())
                .votedOption(voteRequest.getVote())
                .build();
        voteRepository.save(vote);

        votingSession.getVotes().add(vote);
        votingSessionRepository.save(votingSession);

    }

    private VotingSession getSessaoVotacao(Long pautaId) {
        return votingSessionRepository.findNonExpiredByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting session not found"));
    }

}
