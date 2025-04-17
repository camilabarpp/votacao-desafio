package com.votacao.desafio.dto;

import com.votacao.desafio.entity.Pauta;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PautaResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public static PautaResponse toResponse(Pauta pauta) {
        return PautaResponse.builder()
                .id(pauta.getId())
                .title(pauta.getTitle())
                .description(pauta.getDescription())
                .createdAt(pauta.getCreatedAt())
                .build();
    }
}