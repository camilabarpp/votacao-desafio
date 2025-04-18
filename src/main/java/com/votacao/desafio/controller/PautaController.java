package com.votacao.desafio.controller;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pauta")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping()
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaRequest) {
        PautaResponse pautaResponse = pautaService.createPauta(pautaRequest);
        return ResponseEntity.ok(pautaResponse);
    }

    @GetMapping()
    public ResponseEntity<Page<PautaResponse>> listAllPautas(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PautaResponse> pautas = pautaService.listAllPautas(status, page, size);
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("{pautaId}")
    public ResponseEntity<PautaResponse> getPautaById(@PathVariable Long pautaId) {
        PautaResponse pauta = pautaService.getPautaResponseById(pautaId);
        return ResponseEntity.ok(pauta);
    }
}
