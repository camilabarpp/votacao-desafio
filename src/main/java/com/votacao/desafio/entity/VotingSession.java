package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "sessao_votos")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @CreatedDate
    @Column(name = "data_inicio")
    private LocalDateTime votingSessionStartedAt;

    @Column(name = "data_fim")
    private LocalDateTime votingSessionEndedAt;

    @OneToMany(mappedBy = "votingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

    public VotingSessionStatus getVotingSessionStatus() {
        LocalDateTime now = LocalDateTime.now();

        boolean hasStarted = votingSessionStartedAt != null && votingSessionStartedAt.isBefore(now);
        boolean isStillOpen = votingSessionEndedAt == null || votingSessionEndedAt.isAfter(now);

        if (hasStarted && isStillOpen) {
            return VotingSessionStatus.OPEN;
        } else {
            return VotingSessionStatus.CLOSED;
        }
    }

    public enum VotingSessionStatus {
        OPEN,
        CLOSED
    }
}