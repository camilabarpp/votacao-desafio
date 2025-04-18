package com.votacao.desafio.service;

import com.votacao.desafio.dto.*;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.AssociateRepository;
import com.votacao.desafio.repository.PautaRepository;
import com.votacao.desafio.repository.VoteRepository;
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

    public Page<PautaResponse> listAllPautas(String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (status == null) status = "";

        if (!status.isEmpty()) {
            switch (status) {
                case "OPEN" -> {
                    return votingsessionService.listAllVotingSessionsOpen(page, size)
                            .map(PautaResponse::mapToPautaResponse);
                }
                case "CLOSED" -> {
                    return votingsessionService.listAllVotingSessionsClosed(page, size)
                            .map(PautaResponse::mapToPautaResponse);
                }

                default -> {
                    return pautaRepository.findAll(pageable)
                            .map(PautaResponse::mapToPautaResponse);
                }
            }
        }

        return pautaRepository.findAll(pageable)
                .map(PautaResponse::mapToPautaResponse);
    }

    public PautaResponse getPautaById(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found with ID: " + pautaId));

        return PautaResponse.mapToPautaResponse(pauta);
    }

    public VotingSessionResponse getVotingSessionById(Long votingSessionId) {
        VotingSession currentVotingSession = votingsessionService.getVotingSessionById(votingSessionId);
        return mapToVotingSessionResponse(currentVotingSession);
    }

    public PautaResponse createPauta(PautaRequest pautaRequest) {
        log.info("Creating new Pauta with title: {}", pautaRequest.getTitle());
        Pauta pauta = Pauta.builder()
                .title(pautaRequest.getTitle())
                .description(pautaRequest.getDescription())
                .build();

        Pauta savedPauta = pautaRepository.save(pauta);
        return PautaResponse.mapToPautaResponse(savedPauta);
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
