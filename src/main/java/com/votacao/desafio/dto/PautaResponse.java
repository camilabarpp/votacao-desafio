package com.votacao.desafio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.votacao.desafio.dto.VotingSessionResponse.mapToVotingSessionResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PautaResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private VotingSessionResponse votingSession;

    public static PautaResponse mapToPautaResponse(Pauta pauta) {
        PautaResponse response = new PautaResponse();
        response.setId(pauta.getId());
        response.setTitle(pauta.getTitle());
        response.setDescription(pauta.getDescription());
        response.setCreatedAt(pauta.getCreatedAt());

        if (pauta.getVotingSession() != null) {
            VotingSessionResponse votingSessions = mapToVotingSessionResponse(pauta.getVotingSession());
            response.setVotingSession(votingSessions);
        }

        return response;
    }

    public static PautaResponse mapToPautaResponse(VotingSession votingSession) {
        PautaResponse response = new PautaResponse();
        response.setId(votingSession.getId());
        response.setTitle(votingSession.getPauta().getTitle());
        response.setDescription(votingSession.getPauta().getDescription());
        response.setCreatedAt(votingSession.getPauta().getCreatedAt());

        VotingSessionResponse votingSessions = mapToVotingSessionResponse(votingSession.getPauta().getVotingSession());
        response.setVotingSession(votingSessions);

        return response;
    }
}