package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessoes_votacao")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
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

    @Builder.Default
    @OneToMany(mappedBy = "votingSession", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Vote> votes = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}