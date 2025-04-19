package com.votacao.desafio.dto;

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
    private LocalDateTime votingSessionStartedAt;
    private LocalDateTime votingSessionEndedAt;
    private String votingSessionStatus;
    @Builder.Default
    private List<VoteResponse> votes = List.of();

    public static VotingSessionResponse mapToVotingSessionResponse(VotingSession votingSession) {
        VotingSessionResponse response = new VotingSessionResponse();
        response.setId(votingSession.getId());
        response.setVotingSessionStartedAt(votingSession.getVotingSessionStartedAt());
        response.setVotingSessionEndedAt(votingSession.getVotingSessionEndedAt());
        response.setVotingSessionStatus(votingSession.getVotingSessionStatus().name());

        if (votingSession.getVotes() != null) {
            response.setVotes(votingSession.getVotes().stream()
                    .map(VoteResponse::mapToVoteResponse)
                    .toList());
        }

        return response;
    }
}