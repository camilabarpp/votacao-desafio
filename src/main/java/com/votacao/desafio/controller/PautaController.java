package com.votacao.desafio.controller;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.votacao.desafio.dto.PautaResponse.toResponse;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @GetMapping
    public ResponseEntity<Page<PautaResponse>> listPauta(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Pauta> pautas = pautaService.listAllPautas(page, size);
        return ResponseEntity.ok(pautas
                .map(PautaResponse::toResponse));
    }

    @PostMapping
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaRequest) {
        Pauta novaPauta = pautaService.createPauta(pautaRequest);
        return ResponseEntity.status(201).body(toResponse(novaPauta));
    }

    @PostMapping("/{pautaId}/votos")
    public ResponseEntity<Void> registerVote(@PathVariable Long pautaId,
                                             @RequestBody VoteRequest voteRequest) {
        pautaService.registerVote(pautaId, voteRequest);
        return ResponseEntity.status(202).build();
    }
//
//    @GetMapping("/{id}/resultado")
//    public ResponseEntity<ResultadoVotacaoDTO> resultado(@PathVariable Long id) {
//        // implementação
//    }
}
