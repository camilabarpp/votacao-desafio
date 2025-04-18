package com.votacao.desafio.repository;

import com.votacao.desafio.entity.VotingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    @Query("SELECT sv FROM VotingSession sv WHERE sv.pauta.id = :pautaId " +
            "AND sv.votingSessionStartedAt <= :now " +
            "AND (sv.votingSessionEndedAt IS NULL OR sv.votingSessionEndedAt > :now)")
    Optional<VotingSession> findOpenVotingSessionByPautaId(Long pautaId, LocalDateTime now);

    default Optional<VotingSession> findOpenVotingSessionByPautaId(Long pautaId) {
        return findOpenVotingSessionByPautaId(pautaId, LocalDateTime.now());
    }

    @Query("SELECT sv FROM VotingSession sv WHERE " +
            "sv.votingSessionStartedAt IS NOT NULL AND " +
            "sv.votingSessionStartedAt <= :now AND " +
            "(sv.votingSessionEndedAt IS NULL OR sv.votingSessionEndedAt > :now)")
    Page<VotingSession> listAllVotingSessionsOpen(LocalDateTime now, Pageable pageable);

    @Query("SELECT sv FROM VotingSession sv WHERE " +
            "sv.votingSessionStartedAt IS NOT NULL AND " +
            "sv.votingSessionStartedAt <= :now AND " +
            "sv.votingSessionEndedAt IS NOT NULL AND " +
            "sv.votingSessionEndedAt <= :now")
    Page<VotingSession> listAllVotingSessionsClosed(LocalDateTime now, Pageable pageable);
}