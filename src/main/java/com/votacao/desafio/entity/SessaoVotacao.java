package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessoes_votacao")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Pauta pauta;

    private LocalDateTime inicioSessao;
    private LocalDateTime fimSessao;
    private boolean sessaoAberta;
}