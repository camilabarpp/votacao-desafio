package com.votacao.desafio.dto;

import lombok.Getter;
import jakarta.validation.constraints.NotNull;

@Getter
public class PautaRequest {
    @NotNull(message = "Título é obrigatório")
    private String title;
    @NotNull(message = "Descrição é obrigatória")
    private String description;
}