package com.votacao.desafio.service;

import com.votacao.desafio.dto.AssociateRequest;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.repository.AssociateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssociateService {

    private final AssociateRepository associateRepository;

    public Page<Associate> getAllAssociates(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return associateRepository.findAll(pageable);
    }

    public Associate getAssociateById(Long associateId) {
        return associateRepository.findById(associateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associate not found with ID: " + associateId));
    }

    public Associate getAssociateByCpf(String cpf) {
        return associateRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associate not found with CPF: " + cpf));
    }

    public Associate createAssociate(AssociateRequest associateRequest) {
        boolean existsByCpf = associateRepository.existsByCpf(associateRequest.getCpf());
        boolean existsByEmail = associateRepository.existsByEmail(associateRequest.getEmail());

        if (existsByCpf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate with CPF already exists: " + associateRequest.getCpf());
        }

        if (existsByEmail) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate with email already exists: " + associateRequest.getEmail());
        }

        return associateRepository.save(Associate.builder()
                .name(associateRequest.getName())
                .cpf(associateRequest.getCpf())
                .email(associateRequest.getEmail())
                .build());
    }

    public Associate updateAssociate(Long associateId, AssociateRequest associateRequest) {
        Associate existingAssociate = getAssociateById(associateId);

        if (!existingAssociate.getCpf().equals(associateRequest.getCpf())) {
            boolean existsByCpf = associateRepository.existsByCpf(associateRequest.getCpf());
            if (existsByCpf) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate with CPF already exists: " + associateRequest.getCpf());
            }
        }

        if (!existingAssociate.getEmail().equals(associateRequest.getEmail())) {
            boolean existsByEmail = associateRepository.existsByEmail(associateRequest.getEmail());
            if (existsByEmail) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associate with email already exists: " + associateRequest.getEmail());
            }
        }

        existingAssociate.setName(associateRequest.getName());
        existingAssociate.setCpf(associateRequest.getCpf());
        existingAssociate.setEmail(associateRequest.getEmail());

        return associateRepository.save(existingAssociate);
    }

    public void deleteAssociate(Long associateId) {
        Associate existingAssociate = getAssociateById(associateId);
        associateRepository.delete(existingAssociate);
    }
}
