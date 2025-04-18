package com.votacao.desafio.dto;

import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

@Getter
@Setter
public class PautaRequest {
    @NotNull(message = "Title is required")
    private String title;
    @NotNull(message = "Description is required")
    private String description;
}