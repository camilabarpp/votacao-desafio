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
public class VotingSessionListResponse {
    private Long id;
    private Pauta pauta;
    private LocalDateTime votingSessionStartedAt;
    private LocalDateTime votingSessionEndedAt;
    private boolean votingSessionOpen;

    private Integer votesCount;

    private int votesCountYes;

    private Integer votesCountNo;

    public static VotingSessionListResponse toResponse(VotingSession votingSession) {
        List<Vote> votes = votingSession.getVotes();
        int votesCountYes = (int) votes.stream()
                .filter(vote -> vote.getVotedOption() == Vote.VoteOption.YES)
                .count();
        int votesCountNo = (int) votes.stream()
                .filter(vote -> vote.getVotedOption() == Vote.VoteOption.NO)
                .count();

        return VotingSessionListResponse.builder()
                .id(votingSession.getId())
                .pauta(votingSession.getPauta())
                .votingSessionStartedAt(votingSession.getVotingSessionStartedAt())
                .votingSessionEndedAt(votingSession.getVotingSessionEndedAt())
                .votingSessionOpen(votingSession.isVotingSessionOpen())
                .votesCount(votes.size())
                .votesCountYes(votesCountYes)
                .votesCountNo(votesCountNo)
                .build();
    }
}