package com.votacao.desafio.controller;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.service.PautaManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaManagementService pautaManagementService;

    @PostMapping()
    public ResponseEntity<PautaResponse> createPauta(@RequestBody @Valid PautaRequest pautaRequest) {
        PautaResponse pautaResponse = pautaManagementService.createPauta(pautaRequest);
        return new ResponseEntity<>(pautaResponse, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Page<PautaResponse>> listAllPautas(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PautaResponse> pautas = pautaManagementService.listAllPautas(status, page, size);
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("{pautaId}")
    public ResponseEntity<PautaResponse> getPautaById(@PathVariable Long pautaId) {
        PautaResponse pauta = pautaManagementService.getPautaResponseById(pautaId);
        return ResponseEntity.ok(pauta);
    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<VotingResultResponse> getVotingResult(@PathVariable Long pautaId) {
        VotingResultResponse votingResult = pautaManagementService.getVotingResult(pautaId);
        return ResponseEntity.ok(votingResult);
    }
}
