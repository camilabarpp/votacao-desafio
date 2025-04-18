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
        List<VoteResponse> voteResponses = votes.stream().map(voto -> {
                    VoteResponse voteResponse = new VoteResponse();
                    voteResponse.setId(voto.getId());
                    voteResponse.setAssociateId(voto.getAssociate().getId());
                    voteResponse.setAssociateName(voto.getAssociate().getName());
                    voteResponse.setAssociateCpf(voto.getAssociate().getCpf());
                    voteResponse.setVotedOption(voto.getVoteOption().name());
                    voteResponse.setVotedAt(voto.getVotedAt());
                    return voteResponse;
                })
                .toList();
        sessionResponse.setVotes(voteResponses);

        sessionResponse.setVotesCount(votes.size());
        sessionResponse.setVotesCountYes((int) votes.stream()
                .filter(vote -> "YES".equals(vote.getVoteOption().name()))
                .count());
        sessionResponse.setVotesCountNo((int) votes.stream()
                .filter(vote -> "NO".equals(vote.getVoteOption().name()))
                .count());

        resultado.setVotingSession(sessionResponse);

        return resultado;
    }
}