package com.votacao.desafio.dto;

import com.votacao.desafio.entity.Vote;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteRequest {
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    @NotNull(message = "Associate ID is required")
    private Long associatedId;
    @NotNull(message = "Vote is required")
    private Vote.VoteOption vote;
}