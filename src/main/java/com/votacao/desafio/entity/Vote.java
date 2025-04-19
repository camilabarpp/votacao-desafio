package com.votacao.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        YES, NO;

        public static String getValues() {
            StringBuilder values = new StringBuilder();
            for (VoteOption voteOption : VoteOption.values()) {
                values.append(voteOption.name()).append(", ");
            }
            return values.substring(0, values.length() - 2);
        }

        public static VoteOption fromInput(String input) {
            for (VoteOption voteOption : VoteOption.values()) {
                if (voteOption.name().equalsIgnoreCase(input)) {
                    return voteOption;
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vote option: " + input + ". Valid options are: " + getValues());
        }
    }
}