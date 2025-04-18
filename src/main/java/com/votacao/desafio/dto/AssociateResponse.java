package com.votacao.desafio.dto;

import com.votacao.desafio.entity.Associate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociateResponse {
    private Long id;
    private String name;
    private String cpf;
    private String email;

    public static AssociateResponse fromEntity(Associate associate) {
        return AssociateResponse.builder()
                .id(associate.getId())
                .name(associate.getName())
                .cpf(associate.getCpf())
                .email(associate.getEmail())
                .build();
    }
}
