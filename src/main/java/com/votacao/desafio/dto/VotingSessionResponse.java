package com.votacao.desafio.dto;

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
    private LocalDateTime votingSessionStartedAt;
    private LocalDateTime votingSessionEndedAt;
    private String votingSessionStatus;
    @Builder.Default
    private List<VoteResponse> votes = List.of();
    @Builder.Default
    private Integer votesCount = 0;
    @Builder.Default
    private Integer votesCountYes = 0;
    @Builder.Default
    private Integer votesCountNo = 0;

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
            List<Vote> votes = votingSession.getVotes();
            response.setVotesCount(votes.size());

            long yesVoteCount = votes.stream()
                    .filter(vote -> "YES".equals(vote.getVoteOption().name()))
                    .count();
            response.setVotesCountYes((int) yesVoteCount);

            long noVoteCount = votes.stream()
                    .filter(vote -> "NO".equals(vote.getVoteOption().name()))
                    .count();
            response.setVotesCountNo((int) noVoteCount);
        }

        return response;
    }
}