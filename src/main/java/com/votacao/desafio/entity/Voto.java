package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "votos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SessaoVotacao sessao;

    private String idAssociado;
    private boolean voto;
    private LocalDateTime dataVoto;
}