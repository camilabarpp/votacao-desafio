package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "votos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voting_session_id")
    private VotingSession votingSession;

    @Column(name = "associated_id")
    private String associatedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "voted_option")
    private VoteOption votedOption;

    @CreatedDate
    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    public enum VoteOption {
        YES,
        NO
    }
}