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
public class VotingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    @Column(name = "voting_session_started_at")
    private LocalDateTime votingSessionStartedAt;

    @Column(name = "voting_session_ended_at")
    private LocalDateTime votingSessionEndedAt;

    @Column(name = "voting_session_open")
    private boolean votingSessionOpen;
}