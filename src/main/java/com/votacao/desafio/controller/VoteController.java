package com.votacao.desafio.controller;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import com.votacao.desafio.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votos")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/pauta/{pautaId}")
    public ResponseEntity<VotingResultResponse> registerVote(
            @PathVariable Long pautaId,
            @RequestBody VoteRequest voteRequest) {
        return ResponseEntity.ok(voteService.registerVote(pautaId, voteRequest));
    }
}