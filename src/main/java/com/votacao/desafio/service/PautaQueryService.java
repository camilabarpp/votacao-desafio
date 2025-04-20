package com.votacao.desafio.service;

import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaQueryService {

    private final PautaRepository pautaRepository;

    @Transactional
    public Pauta getPautaById(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found with ID: " + pautaId));
    }

    @Transactional
    public Pauta save(Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    @Transactional(readOnly = true)
    public Page<Pauta> findAll(Pageable pageable) {
        return pautaRepository.findAll(pageable);
    }

    public void delete(Pauta pauta) {
        pautaRepository.delete(pauta);
    }
}