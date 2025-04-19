package com.votacao.desafio.service;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.votacao.desafio.dto.VotingResultResponse.buildVotingResultResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final PautaQueryService pautaService;
    private final VotingSessionService votingSessionService;
    private final AssociateService associateService;
    private final VoteRepository voteRepository;
    private final CpfValidationService cpfValidationService;

    @Transactional
    public VotingResultResponse registerVote(Long pautaId, VoteRequest voteRequest) {
        log.info("Registering vote for Pauta with ID: {}", pautaId);
        Pauta pauta = pautaService.getPautaById(pautaId);

        if (pauta.getVotingSession() == null) {
            log.error("Pauta with ID {} does not have an voting session opened", pautaId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pauta with ID " + pautaId + " does not have an voting session opened");
        }

        VotingSession votingSession = votingSessionService.findById(pauta.getVotingSession().getId());
        String associateCpf = voteRequest.getCpf();

        if (cpfValidationService.validateCpf(associateCpf).equals(CpfValidationService.StatusVotacao.UNABLE_TO_VOTE)) {
            log.error("Invalid CPF {}", associateCpf);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CPF " + associateCpf);
        }

        Associate associate = associateService.getAssociateByCpf(associateCpf);

        if (votingSession.getVotingSessionStatus().name().equals("CLOSED")) {
            log.error("Voting session {} is closed", votingSession.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session is closed");
        }

        boolean associateVoted = votingSession.getVotes().stream().anyMatch(vote -> vote.getAssociate().getId().equals(associate.getId()));
        if (associateVoted) {
            log.error("Associate {} already voted", associate.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate already voted");
        }
        Vote vote = voteRepository.save(Vote.builder()
                .votingSession(votingSession)
                .associate(associate)
                .voteOption(voteRequest.getVote())
                .build());

        votingSession.getVotes().add(vote);
        votingSessionService.saveVotingSession(votingSession);

        return buildVotingResultResponse(pauta, votingSession);
    }

}
