package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.repository.PautaRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotingSessionService votingSessionService;

    @Transactional
    public PautaResponse createPauta(PautaRequest pautaRequest) {
        log.info("Creating new Pauta with title: {}", pautaRequest.getTitle());
        Pauta pauta = Pauta.builder()
                .title(pautaRequest.getTitle())
                .description(pautaRequest.getDescription())
                .build();

        Pauta savedPauta = pautaRepository.save(pauta);
        return PautaResponse.mapToPautaResponse(savedPauta);
    }

    public PautaResponse getPautaResponseById(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found with ID: " + pautaId));

        return PautaResponse.mapToPautaResponse(pauta);
    }

    @Transactional
    public Pauta getPautaById(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found with ID: " + pautaId));
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
                    return pautaRepository.findAll(pageable)
                            .map(PautaResponse::mapToPautaResponse);
                }
            }
        }

        return pautaRepository.findAll(pageable)
                .map(PautaResponse::mapToPautaResponse);
    }
}