package com.votacao.desafio.controller;

import com.votacao.desafio.dto.*;
import com.votacao.desafio.service.VotacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votacao")
public class VotacaoController {

    @Autowired
    private VotacaoService votacaoService;

    @PostMapping("/pauta")
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaRequest) {
        PautaResponse pautaResponse = votacaoService.createPauta(pautaRequest);
        return ResponseEntity.ok(pautaResponse);
    }

    @GetMapping("/pauta")
    public ResponseEntity<Page<PautaResponse>> listAllPautas(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PautaResponse> pautas = votacaoService.listAllPautas(status, page, size);
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("/pauta/{pautaId}")
    public ResponseEntity<PautaResponse> getPautaById(@PathVariable Long pautaId) {
        PautaResponse pauta = votacaoService.getPautaById(pautaId);
        return ResponseEntity.ok(pauta);
    }

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