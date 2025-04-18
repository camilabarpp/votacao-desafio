package com.votacao.desafio.controller;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votacao")
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;

    @PostMapping("/pauta/{pautaId}/sessao")
    public ResponseEntity<VotingSessionResponse> openVotingSessionByPautaId(@PathVariable Long pautaId,
                                                                            @RequestParam(defaultValue = "1") Integer votingSessionDurationInMinutes) {
        VotingSessionResponse openedSession = votacaoService.openVotingSessionByPautaId(pautaId, votingSessionDurationInMinutes);
        return ResponseEntity.ok(openedSession);
    }

    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<VotingSessionResponse> getVotingSessionById(@PathVariable Long sessaoId) {
        VotingSessionResponse sessao = votacaoService.getVotingSessionById(sessaoId);
        return ResponseEntity.ok(sessao);
    }

    @PostMapping("/pauta/{pautaId}/voto")
    public ResponseEntity<VotingResultResponse> registerVote(
            @PathVariable Long pautaId,
            @RequestBody VoteRequest voteRequest) {
        return ResponseEntity.ok(votacaoService.registerVote(pautaId, voteRequest));
    }

    @GetMapping("/pauta/{pautaId}/resultado")
    public ResponseEntity<VotingResultResponse> consultarResultado(@PathVariable Long pautaId) {
        VotingResultResponse resultado = votacaoService.consultarResultadoVotacao(pautaId);
        return ResponseEntity.ok(resultado);
    }
}