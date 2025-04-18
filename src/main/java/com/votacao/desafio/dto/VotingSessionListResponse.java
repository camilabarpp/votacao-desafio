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
    private String votingSessionStatus;

    private Integer votesCount;

    private int votesCountYes;

    private Integer votesCountNo;

    public static VotingSessionListResponse toResponse(VotingSession votingSession) {
        List<Vote> votes = votingSession.getVotes();
        int votesCountYes = (int) votes.stream()
                .filter(vote -> vote.getVoteOption().name().equals("YES"))
                .count();
        int votesCountNo = (int) votes.stream()
                .filter(vote -> vote.getVoteOption().name().equals("NO"))
                .count();

        return VotingSessionListResponse.builder()
                .id(votingSession.getId())
                .pauta(votingSession.getPauta())
                .votingSessionStartedAt(votingSession.getVotingSessionStartedAt())
                .votingSessionEndedAt(votingSession.getVotingSessionEndedAt())
                .votingSessionStatus(votingSession.getVotingSessionStatus().name())
                .votesCount(votes.size())
                .votesCountYes(votesCountYes)
                .votesCountNo(votesCountNo)
                .build();
    }
}