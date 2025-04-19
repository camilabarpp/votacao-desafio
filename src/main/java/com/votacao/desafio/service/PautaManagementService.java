package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
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

import static com.votacao.desafio.dto.VotingResultResponse.buildVotingResultResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaManagementService {

    private final PautaQueryService pautaQueryService;
    private final VotingSessionService votingSessionService;

    @Transactional
    public PautaResponse createPauta(PautaRequest pautaRequest) {
        log.info("Creating new Pauta with title: {}", pautaRequest.getTitle());
        Pauta pauta = Pauta.builder()
                .title(pautaRequest.getTitle())
                .description(pautaRequest.getDescription())
                .build();

        Pauta savedPauta = pautaQueryService.save(pauta);
        return PautaResponse.mapToPautaResponse(savedPauta);
    }

    @Transactional
    public Page<PautaResponse> listAllPautas(String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (status == null) status = "";

        if (!status.isEmpty()) {
            switch (status) {
                case "OPEN" -> {
                    return votingSessionService.listAllVotingSessionsOpen(page, size)
                            .map(PautaResponse::mapToPautaResponse);
                }
                case "CLOSED" -> {
                    return votingSessionService.listAllVotingSessionsClosed(page, size)
                            .map(PautaResponse::mapToPautaResponse);
                }

                default -> {
                    return pautaQueryService.findAll(pageable)
                            .map(PautaResponse::mapToPautaResponse);
                }
            }
        }

        return pautaQueryService.findAll(pageable)
                .map(PautaResponse::mapToPautaResponse);
    }

    public PautaResponse getPautaResponseById(Long pautaId) {
        Pauta pauta = pautaQueryService.getPautaById(pautaId);
        return PautaResponse.mapToPautaResponse(pauta);
    }

    public VotingResultResponse getVotingResult(Long pautaId) {
        Pauta pauta = pautaQueryService.getPautaById(pautaId);

        VotingSession votingSession = votingSessionService.getOpenVotingSessionById(pautaId);

        if (pauta.getVotingSession() == null) {
            log.error("Pauta with ID {} does not have an voting session opened", pautaId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pauta with ID " + pautaId + " does not have an voting session opened");
        }

        return buildVotingResultResponse(pauta, votingSession);
    }
}