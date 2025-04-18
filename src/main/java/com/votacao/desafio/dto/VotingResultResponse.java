package com.votacao.desafio.dto;

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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VotingSessionDTO {
        private LocalDateTime votingSessionStartedAt;
        private LocalDateTime votingSessionEndedAt;
        private String votingSessionStatus;
        private List<VoteResponse> votes;
        private Integer votesCount;
        private Integer votesCountYes;
        private Integer votesCountNo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VotoDTO {
        private Long id;
        private Long associateId;
        private String associateName;
        private String associateCpf;
        private String votedOption;
        private LocalDateTime votedAt;
    }
}