package com.votacao.desafio.controller;

import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.votacao.desafio.dto.VotingSessionResponse.toResponse;

@RestController
@RequestMapping("/v1/sessoes")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @PostMapping("/pauta/{pautaId}")
    public ResponseEntity<VotingSessionResponse> openVotingSession(@PathVariable Long pautaId,
                                                                   @RequestParam(value = "sessionDuration", defaultValue = "1") Integer  sessionDuration) {
        VotingSession openVotingSession = votingSessionService.openVotingSession(pautaId, sessionDuration);
        return ResponseEntity.status(201).body(toResponse(openVotingSession));
    }
}
