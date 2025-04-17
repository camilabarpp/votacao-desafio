package com.votacao.desafio.controller;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.votacao.desafio.dto.PautaResponse.toResponse;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final VotacaoService votacaoService;

    @GetMapping
    public ResponseEntity<Page<PautaResponse>> listPauta(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Pauta> pautas = votacaoService.listAllPautas(page, size);
        return ResponseEntity.ok(pautas
                .map(PautaResponse::toResponse));
    }

    @PostMapping
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaRequest) {
        Pauta novaPauta = votacaoService.createPauta(pautaRequest);
        return ResponseEntity.status(201).body(toResponse(novaPauta));
    }

    @PostMapping("/{id}/sessao")
    public ResponseEntity<VotingSession> openVotingSession(@PathVariable Long id,
                                                           @RequestParam(value = "sessionDuration", defaultValue = "1") Integer  sessionDuration) {
        VotingSession openVotingSession = votacaoService.openVotingSession(id, sessionDuration);
        return ResponseEntity.status(201).body(openVotingSession);
    }

    @PostMapping("/{pautaId}/votos")
    public ResponseEntity<Void> registerVote(@PathVariable Long pautaId,
                                             @RequestBody VoteRequest voteRequest) {
        votacaoService.registerVote(pautaId, voteRequest);
        return ResponseEntity.status(201).build();
    }
//
//    @GetMapping("/{id}/resultado")
//    public ResponseEntity<ResultadoVotacaoDTO> resultado(@PathVariable Long id) {
//        // implementação
//    }
}
