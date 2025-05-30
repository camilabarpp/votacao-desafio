package com.votacao.desafio.controller;

import com.votacao.desafio.common.swagger.VotingSessionSwagger;
import com.votacao.desafio.dto.VotingSessionResponse;
import com.votacao.desafio.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessoes")
@RequiredArgsConstructor
public class VotingSessionController implements VotingSessionSwagger {

    private final VotingSessionService votingSessionService;

    @GetMapping
    public ResponseEntity<Page<VotingSessionResponse>> listAllVotingSessions(
            @RequestParam(required = false) String votingSessionStatus,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(votingSessionService.listAllVotingSessions(votingSessionStatus, page, size));
    }

    @PostMapping("/pauta/{pautaId}")
    public ResponseEntity<VotingSessionResponse> openVotingSessionByPautaId(@PathVariable Long pautaId,
                                                                            @RequestParam(defaultValue = "1") Integer votingSessionDurationInMinutes) {
        VotingSessionResponse openedSession = votingSessionService.openVotingSessionByPautaId(pautaId, votingSessionDurationInMinutes);
        return new ResponseEntity<>(openedSession, HttpStatus.CREATED);
    }

    @GetMapping("/{sessaoId}")
    public ResponseEntity<VotingSessionResponse> getVotingSessionById(@PathVariable Long sessaoId) {
        VotingSessionResponse sessao = votingSessionService.getVotingSessionById(sessaoId);
        return ResponseEntity.ok(sessao);
    }

    @PutMapping("/{sessaoId}")
    public ResponseEntity<VotingSessionResponse> updateVotingSession(@PathVariable Long sessaoId, @RequestParam(defaultValue = "1") Integer votingSessionDurationInMinutes) {
        VotingSessionResponse updatedSession = votingSessionService.updateVotingSession(sessaoId, votingSessionDurationInMinutes);
        return ResponseEntity.ok(updatedSession);
    }

    @PatchMapping("/{sessaoId}/fechar")
    public ResponseEntity<VotingSessionResponse> closeVotingSession(@PathVariable Long sessaoId) {
        VotingSessionResponse closedSession = votingSessionService.closeVotingSession(sessaoId);
        return ResponseEntity.ok(closedSession);
    }

    @DeleteMapping("/{sessaoId}")
    public ResponseEntity<Void> deleteVotingSession(@PathVariable Long sessaoId) {
        votingSessionService.deleteVotingSession(sessaoId);
        return ResponseEntity.noContent().build();
    }
}