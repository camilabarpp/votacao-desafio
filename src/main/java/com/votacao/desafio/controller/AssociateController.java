package com.votacao.desafio.controller;

import com.votacao.desafio.dto.AssociateRequest;
import com.votacao.desafio.dto.AssociateResponse;
import com.votacao.desafio.service.AssociateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/associados")
@RequiredArgsConstructor
public class AssociateController {

    private final AssociateService associateService;

    @GetMapping
    public ResponseEntity<Page<AssociateResponse>> getAllAssociates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(associateService.getAllAssociates(page, size)
                .map(AssociateResponse::fromEntity));
    }

    @GetMapping("/{associateId}")
    public ResponseEntity<AssociateResponse> getAssociateById(@PathVariable Long associateId) {
        return ResponseEntity.ok(AssociateResponse.fromEntity(associateService.getAssociateById(associateId)));
    }

    @PostMapping
    public ResponseEntity<AssociateResponse> createAssociate(@RequestBody @Valid AssociateRequest associateRequest) {
        return new ResponseEntity<>(AssociateResponse.fromEntity(associateService.createAssociate(associateRequest)), HttpStatus.CREATED);
    }

    @PutMapping("/{associateId}")
    public ResponseEntity<AssociateResponse> updateAssociate(@PathVariable Long associateId, @RequestBody @Valid AssociateRequest associateRequest) {
        return ResponseEntity.ok(AssociateResponse.fromEntity(associateService.updateAssociate(associateId, associateRequest)));
    }

    @DeleteMapping("/{associateId}")
    public ResponseEntity<Void> deleteAssociate(@PathVariable Long associateId) {
        associateService.deleteAssociate(associateId);
        return ResponseEntity.noContent().build();
    }
}