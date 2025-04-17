package com.votacao.desafio.dto;

import lombok.Getter;
import jakarta.validation.constraints.NotNull;

@Getter
public class PautaRequest {
    @NotNull(message = "Title is required")
    private String title;
    @NotNull(message = "Description is required")
    private String description;
}