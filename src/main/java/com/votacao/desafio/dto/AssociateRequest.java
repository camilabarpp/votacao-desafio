package com.votacao.desafio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssociateRequest {
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "CPF is required")
    private String cpf;
    @NotNull(message = "Email is required")
    private String email;
}