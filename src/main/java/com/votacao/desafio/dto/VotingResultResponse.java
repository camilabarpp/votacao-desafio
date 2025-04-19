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
public class VotingResultResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private VotingSessionResponse votingSession;
    private PautaResultResponse votingResult;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PautaResultResponse {
        private Integer votesCount;
        private Integer votesCountYes;
        private Integer votesCountNo;
        private Double percentageYes;
        private Double percentageNo;
        private VotingResult result;

        public enum VotingResult {
            APPROVED,
            REPROVED,
            TIE,
            IN_PROGRESS
        }
    }

    public static VotingResultResponse buildVotingResultResponse(Pauta pauta, VotingSession votingSession) {
        VotingResultResponse resultado = new VotingResultResponse();
        resultado.setId(pauta.getId());
        resultado.setTitle(pauta.getTitle());
        resultado.setDescription(pauta.getDescription());
        resultado.setCreatedAt(pauta.getCreatedAt());

        VotingSessionResponse sessionResponse = VotingSessionResponse.builder()
                .id(votingSession.getId())
                .votingSessionStartedAt(votingSession.getVotingSessionStartedAt())
                .votingSessionEndedAt(votingSession.getVotingSessionEndedAt())
                .votingSessionStatus(votingSession.getVotingSessionStatus().name())
                .build();

        List<Vote> votes = votingSession.getVotes();
        List<VoteResponse> voteResponses = votes.stream()
                .map(VoteResponse::mapToVoteResponse)
                .toList();
        sessionResponse.setVotes(voteResponses);

        int votesCountNo = (int) votes.stream()
                .filter(vote -> "NO".equals(vote.getVoteOption().name()))
                .count();
        int votesCountYes = (int) votes.stream()
                .filter(vote -> "YES".equals(vote.getVoteOption().name()))
                .count();
        PautaResultResponse pautaResult = PautaResultResponse.builder()
                .votesCount(votes.size())
                .votesCountYes(votesCountYes)
                .votesCountNo(votesCountNo)
                .percentageYes((double) votesCountYes / votes.size() * 100)
                .percentageNo((double) votesCountNo / votes.size() * 100)
                .result(calculateVotingResult(votesCountYes, votesCountNo, sessionResponse.getVotingSessionStatus()))
                .build();
        resultado.setVotingResult(pautaResult);
        resultado.setVotingSession(sessionResponse);

        return resultado;
    }

    private static PautaResultResponse.VotingResult calculateVotingResult(Integer votesCountYes, Integer votesCountNo, String votingSessionStatus) {
        if (votingSessionStatus.equals("CLOSED")) {
            if (votesCountYes > votesCountNo) {
                return PautaResultResponse.VotingResult.APPROVED;
            } else if (votesCountYes < votesCountNo) {
                return PautaResultResponse.VotingResult.REPROVED;
            } else {
                return PautaResultResponse.VotingResult.TIE;
            }
        } else {
            return PautaResultResponse.VotingResult.IN_PROGRESS;
        }
    }
}