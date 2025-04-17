package com.votacao.desafio.controller;

import com.votacao.desafio.dto.VotingSessionListResponse;
import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.votacao.desafio.dto.VotingSessionResponse.toResponse;

@RestController
@RequestMapping("/v1/sessoes")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @GetMapping
    public ResponseEntity<Page<VotingSessionListResponse>> listAllVotingSessions(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<VotingSession> votingSessions = votingSessionService.listAllVotingSessions(page, size);
        return ResponseEntity.ok(votingSessions
                .map(VotingSessionListResponse::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotingSessionResponse> getVotingSessionById(@PathVariable Long id) {
        VotingSession votingSession = votingSessionService.getVotingSessionById(id);
        return ResponseEntity.ok(toResponse(votingSession));
    }

    @PostMapping("/pauta/{pautaId}")
    public ResponseEntity<VotingSessionResponse> openVotingSession(@PathVariable Long pautaId,
                                                                   @RequestParam(value = "sessionDuration", defaultValue = "1") Integer  sessionDuration) {
        VotingSession openVotingSession = votingSessionService.openVotingSession(pautaId, sessionDuration);
        return ResponseEntity.status(201).body(toResponse(openVotingSession));
    }
}
