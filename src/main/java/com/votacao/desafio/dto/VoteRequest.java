package com.votacao.desafio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoteRequest {
    @NotNull(message = "Associate CPF is required")
    private String cpf;
    @NotNull(message = "Vote is required")
    private String vote;
}