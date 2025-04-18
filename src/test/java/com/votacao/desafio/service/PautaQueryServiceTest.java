package com.votacao.desafio.service;

import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaQueryServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaQueryService pautaQueryService;

    Pauta pauta;

    @BeforeEach
    void setUp() {
        final String pautaTitle = "Test Pauta";
        final String pautaDescriptions = "Test Description";
        final LocalDateTime createdAt = LocalDateTime.now();

        pauta = Pauta.builder()
                .id(1L)
                .title(pautaTitle)
                .description(pautaDescriptions)
                .createdAt(createdAt)
                .build();
    }

    @Test
    @DisplayName("Should return pauta when given a valid ID")
    void getPautaById_WithExistingId_ShouldReturnPauta() {
        pauta.setId(1L);
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        Pauta result = pautaQueryService.getPautaById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(pautaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when given a non-existing ID")
    void getPautaById_WithNonExistingId_ShouldThrowResponseStatusException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> pautaQueryService.getPautaById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Pauta not found with ID: 1", exception.getReason());
        verify(pautaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return pauta when given a valid ID")
    void save_WithValidPauta_ShouldReturnSavedPauta() {
        when(pautaRepository.save(pauta)).thenReturn(pauta);

        Pauta result = pautaQueryService.save(pauta);

        assertNotNull(result);
        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    @DisplayName("Should return page of pautas when given a valid Pageable")
    void findAll_WithValidPageable_ShouldReturnPageOfPautas() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pauta> pautaPage = new PageImpl<>(Collections.singletonList(pauta));
        when(pautaRepository.findAll(pageable)).thenReturn(pautaPage);

        Page<Pauta> result = pautaQueryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(pautaRepository, times(1)).findAll(pageable);
    }
}