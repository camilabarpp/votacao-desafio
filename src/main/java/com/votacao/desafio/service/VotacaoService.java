package com.votacao.desafio.service;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VoteResponse;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.PautaRepository;
import com.votacao.desafio.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.votacao.desafio.dto.VotingSessionResponse.mapToVotingSessionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final VotingSessionService votingsessionService;
    private final VoteRepository voteRepository;
    private final PautaService pautaService;
    private final AssociateService associateService;

    public VotingSessionResponse getVotingSessionById(Long votingSessionId) {
        VotingSession currentVotingSession = votingsessionService.getVotingSessionById(votingSessionId);
        return mapToVotingSessionResponse(currentVotingSession);
    }

    public VotingSessionResponse openVotingSessionByPautaId(Long pautaId, Integer votingSessionDurationInMinutes) {
        log.info("Opening voting session for Pauta with ID: {}", pautaId);
        Pauta pauta = pautaService.getPautaById(pautaId);

        if (pauta.getVotingSession() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session already exists for this Pauta");
        }

        VotingSession votingSession = votingsessionService.openVotingSession(pauta, votingSessionDurationInMinutes);
        return mapToVotingSessionResponse(votingSession);
    }

    @Transactional
    public VotingResultResponse registerVote(Long pautaId, VoteRequest voteRequest) {
        log.info("Registering vote for Pauta with ID: {}", pautaId);
        Pauta pauta = pautaService.getPautaById(pautaId);

        if (pauta.getVotingSession() == null) {
            log.error("Pauta with ID {} does not have an voting session opened", pautaId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pauta with ID " + pautaId + " does not have an voting session opened");
        }

        VotingSession votingSession = votingsessionService.getVotingSessionById(pauta.getVotingSession().getId());
        Associate associate = associateService.getAssociateByCpf(voteRequest.getCpf());

        if (votingSession.getVotingSessionStatus().name().equals("CLOSED")) {
            log.error("Voting session {} is closed", votingSession.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session is closed");
        }

        if (votingSession.getVotingSessionEndedAt().isBefore(LocalDateTime.now())) {
            log.error("Voting session {} has ended", votingSession.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voting session has ended");
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
        votingsessionService.saveVotingSession(votingSession);

        return gerarResultadoVotacao(pauta, votingSession);
    }

    public VotingResultResponse consultarResultadoVotacao(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new RuntimeException("Pauta não encontrada"));

        VotingSession votingSession = votingsessionService.findOpenVotingSessionByPautaId(pautaId);

        return gerarResultadoVotacao(pauta, votingSession);
    }

    private VotingResultResponse gerarResultadoVotacao(Pauta pauta, VotingSession votingSession) {
        VotingResultResponse resultado = new VotingResultResponse();
        resultado.setId(pauta.getId());
        resultado.setTitle(pauta.getTitle());
        resultado.setDescription(pauta.getDescription());
        resultado.setCreatedAt(pauta.getCreatedAt());

        VotingSessionResponse sessionResponse = VotingSessionResponse.builder()
                .id(votingSession.getId())
                .votingSessionStartedAt(votingSession.getVotingSessionStartedAt())
                .votingSessionEndedAt(votingSession.getVotingSessionEndedAt())
                .votingSessionStatus(votingSession.getVotingSessionStatus().name())
                .build();

        List<Vote> votes = votingSession.getVotes();
        List<VoteResponse> voteResponses = votes.stream().map(voto -> {
                    VoteResponse voteResponse = new VoteResponse();
                    voteResponse.setId(voto.getId());
                    voteResponse.setAssociateId(voto.getAssociate().getId());
                    voteResponse.setAssociateName(voto.getAssociate().getName());
                    voteResponse.setAssociateCpf(voto.getAssociate().getCpf());
                    voteResponse.setVotedOption(voto.getVoteOption().name());
                    voteResponse.setVotedAt(voto.getVotedAt());
                    return voteResponse;
                })
                .toList();
        sessionResponse.setVotes(voteResponses);

        sessionResponse.setVotesCount((int) voteRepository.countVotesBySessionId(votingSession.getId()));
        sessionResponse.setVotesCountYes((int) voteRepository.countYesVotesBySessionId(votingSession.getId()));
        sessionResponse.setVotesCountNo((int) voteRepository.countNoVotesBySessionId(votingSession.getId()));

        resultado.setVotingSession(sessionResponse);

        return resultado;
    }
}
