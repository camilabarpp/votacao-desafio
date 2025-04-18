package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "votos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sessao_id", "associado_id"})
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private VotingSession votingSession;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "associado_id", nullable = false)
    private Associate associate;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcao_voto", nullable = false, length = 3)
    private VoteOption voteOption;

    @CreatedDate
    @Column(name = "data_voto")
    private LocalDateTime votedAt;

    public enum VoteOption {
        YES, NO
    }
}