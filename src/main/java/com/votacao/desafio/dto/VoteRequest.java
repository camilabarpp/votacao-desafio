package com.votacao.desafio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteRequest {
    @NotNull(message = "Pauta ID is required")
    private Long pautaId;
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    @NotNull(message = "Associate ID is required")
    private String associatedId;
    @NotNull(message = "Vote is required")
    private boolean vote;
}