package com.votacao.desafio.dto;

import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.Vote;
import com.votacao.desafio.entity.VotingSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingSessionResponse {
    private Long id;
    private Pauta pauta;
    private LocalDateTime votingSessionStartedAt;
    private LocalDateTime votingSessionEndedAt;
    private boolean votingSessionOpen;
    private List<Vote> votes;

    public static VotingSessionResponse toResponse(VotingSession votingSession) {
        return VotingSessionResponse.builder()
                .id(votingSession.getId())
                .pauta(votingSession.getPauta())
                .votingSessionStartedAt(votingSession.getVotingSessionStartedAt())
                .votingSessionEndedAt(votingSession.getVotingSessionEndedAt())
                .votingSessionOpen(votingSession.isVotingSessionOpen())
                .votes(votingSession.getVotes())
                .build();
    }
}