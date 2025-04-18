package com.votacao.desafio.dto;

import com.votacao.desafio.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {
    private Long id;
    private Long associateId;
    private String associateName;
    private String associateCpf;
    private String email;
    private String votedOption;
    private LocalDateTime votedAt;

    public static VoteResponse mapToVoteResponse(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .associateId(vote.getAssociate().getId())
                .associateName(vote.getAssociate().getName())
                .associateCpf(vote.getAssociate().getCpf())
                .email(vote.getAssociate().getEmail())
                .votedOption(vote.getVoteOption().name())
                .votedAt(vote.getVotedAt())
                .build();
    }
}