package com.votacao.desafio.repository;

import com.votacao.desafio.entity.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Long> {

    Optional<Associate> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}