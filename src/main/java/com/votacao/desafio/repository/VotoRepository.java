package com.votacao.desafio.repository;

import com.votacao.desafio.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Vote, Long> {
}