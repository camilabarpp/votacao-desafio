package com.votacao.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssociateRequest {
    @Schema(
            description = "Name of the associate",
            example = "John Doe"
    )
    @NotNull(message = "Name is required")
    private String name;

    @Schema(
            description = "CPF of the associate, must be a valid CPF",
            example = "72265553018"
    )
    @NotNull(message = "CPF is required")
    private String cpf;

    @Schema(
            description = "Email of the associate, must be a valid email",
            example = "john.doe@example.com"
    )
    @NotNull(message = "Email is required")
    private String email;

    public String getCpf() {
        return cpf.replaceAll("[^0-9]", "");
    }

    public String getEmail() {
        return email.toLowerCase();
    }
}