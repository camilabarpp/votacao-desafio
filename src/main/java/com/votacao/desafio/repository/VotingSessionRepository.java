package com.votacao.desafio.repository;

import com.votacao.desafio.entity.VotingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    @Query("SELECT v FROM VotingSession v WHERE v.votingSessionEndedAt > CURRENT_TIMESTAMP")
    Page<VotingSession> listAllVotingSessionsOpen(Pageable pageable);

    @Query("SELECT v FROM VotingSession v WHERE v.pauta.id = :pautaId AND v.votingSessionEndedAt > CURRENT_TIMESTAMP")
    Optional<VotingSession> findNonExpiredByPautaId(@Param("pautaId") Long pautaId);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VotingSession v WHERE v.pauta.id = :pautaId AND v.votingSessionEndedAt > CURRENT_TIMESTAMP")
    boolean existsByPautaAndVotingSessionOpenTrue(@Param("pautaId") Long pautaId);
}