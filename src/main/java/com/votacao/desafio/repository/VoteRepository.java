package com.votacao.desafio.repository;

import com.votacao.desafio.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVotingSessionIdAndAssociateId(Long votingSessionId, Long associateId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :sessaoId")
    long countVotesBySessionId(Long sessaoId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :sessaoId AND v.voteOption = 'YES'")
    long countYesVotesBySessionId(Long sessaoId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :sessaoId AND v.voteOption = 'NO'")
    long countNoVotesBySessionId(Long sessaoId);
}