package com.votacao.desafio.service;

import com.votacao.desafio.dto.AssociateRequest;
import com.votacao.desafio.entity.Associate;
import com.votacao.desafio.repository.AssociateRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociateServiceTest {

    @Mock
    private AssociateRepository associateRepository;

    @InjectMocks
    private AssociateService associateService;

    private Associate associate;
    private AssociateRequest associateRequest;
    private static final Long ASSOCIATE_ID = 1L;
    private static final String VALID_CPF = "52998224725";
    private static final String VALID_EMAIL = "teste@email.com";
    private static final String VALID_NAME = "João da Silva";

    @BeforeEach
    void setup() {
        associate = Associate.builder()
                .id(ASSOCIATE_ID)
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .email(VALID_EMAIL)
                .build();

        associateRequest = new AssociateRequest();
        associateRequest.setName(VALID_NAME);
        associateRequest.setCpf(VALID_CPF);
        associateRequest.setEmail(VALID_EMAIL);
    }

    @Test
    @DisplayName("Should get all associates paginated")
    void getAllAssociates_ShouldReturnPageOfAssociates() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Associate> associatePage = new PageImpl<>(List.of(associate));

        when(associateRepository.findAll(pageable)).thenReturn(associatePage);
        Page<Associate> result = associateService.getAllAssociates(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(associateRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get associate by ID")
    void getAssociateById_WithValidId_ShouldReturnAssociate() {
        // when
        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.of(associate));
        Associate result = associateService.getAssociateById(ASSOCIATE_ID);

        // then
        assertNotNull(result);
        assertEquals(ASSOCIATE_ID, result.getId());
        verify(associateRepository).findById(ASSOCIATE_ID);
    }

    @Test
    @DisplayName("Should throw exception when associate not found by ID")
    void getAssociateById_WithInvalidId_ShouldThrowException() {
        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.getAssociateById(ASSOCIATE_ID));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Associate not found with ID: " + ASSOCIATE_ID, exception.getReason());
    }

    @Test
    @DisplayName("Should retrieve associate by CPF")
    void getAssociateByCpf_WithValidCpf_ShouldReturnAssociate() {
        when(associateRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(associate));
        Associate result = associateService.getAssociateByCpf(VALID_CPF);

        assertNotNull(result);
        assertEquals(VALID_CPF, result.getCpf());
        verify(associateRepository).findByCpf(VALID_CPF);
    }

    @Test
    @DisplayName("Should throw exception when associate not found by CPF")
    void getAssociateByCpf_WithInvalidCpf_ShouldThrowException() {
        when(associateRepository.findByCpf(VALID_CPF)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.getAssociateByCpf(VALID_CPF));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Associate not found with CPF: " + VALID_CPF, exception.getReason());
    }

    @Test
    @DisplayName("Should create associate")
    void createAssociate_WithValidData_ShouldCreateAssociate() {
        when(associateRepository.existsByCpf(VALID_CPF)).thenReturn(false);
        when(associateRepository.existsByEmail(VALID_EMAIL)).thenReturn(false);
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);

        Associate result = associateService.createAssociate(associateRequest);

        assertNotNull(result);
        assertEquals(VALID_NAME, result.getName());
        assertEquals(VALID_CPF, result.getCpf());
        assertEquals(VALID_EMAIL, result.getEmail());
        verify(associateRepository).save(any(Associate.class));
    }

    @Test
    @DisplayName("Should throw exception when creating associate with existing CPF")
    void createAssociate_WithExistingCpf_ShouldThrowException() {
        when(associateRepository.existsByCpf(VALID_CPF)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.createAssociate(associateRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Associate with CPF already exists: " + VALID_CPF, exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when creating associate with existing email")
    void createAssociate_WithExistingEmail_ShouldThrowException() {
        when(associateRepository.existsByCpf(VALID_CPF)).thenReturn(false);
        when(associateRepository.existsByEmail(VALID_EMAIL)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.createAssociate(associateRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Associate with email already exists: " + VALID_EMAIL, exception.getReason());
    }

    @Test
    @DisplayName("Should update associate")
    void updateAssociate_WithValidData_ShouldUpdateAssociate() {
        String newName = "João Silva Atualizado";
        associateRequest.setName(newName);

        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.of(associate));
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);

        Associate result = associateService.updateAssociate(ASSOCIATE_ID, associateRequest);

        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(associateRepository).save(any(Associate.class));
    }

    @Test
    @DisplayName("Should throw exception when updating associate with existing CPF")
    void updateAssociate_WithExistingCpf_ShouldThrowException() {
        String newCpf = "12345678901";
        associateRequest.setCpf(newCpf);

        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.of(associate));
        when(associateRepository.existsByCpf(newCpf)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.updateAssociate(ASSOCIATE_ID, associateRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Associate with CPF already exists: " + newCpf, exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when updating associate with existing email")
    void updateAssociate_WithExistingEmail_ShouldThrowException() {
        String newEmail = "novo@email.com";
        associateRequest.setEmail(newEmail);

        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.of(associate));
        when(associateRepository.existsByEmail(newEmail)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.updateAssociate(ASSOCIATE_ID, associateRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Associate with email already exists: " + newEmail, exception.getReason());
    }

    @Test
    @DisplayName("Should delete associate")
    void deleteAssociate_WithValidId_ShouldDeleteAssociate() {
        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.of(associate));

        assertDoesNotThrow(() -> associateService.deleteAssociate(ASSOCIATE_ID));
        verify(associateRepository).delete(associate);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent associate")
    void deleteAssociate_WithNonExistentId_ShouldThrowException() {
        when(associateRepository.findById(ASSOCIATE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> associateService.deleteAssociate(ASSOCIATE_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Associate not found with ID: " + ASSOCIATE_ID, exception.getReason());
        verify(associateRepository, never()).delete(any(Associate.class));
        }
}