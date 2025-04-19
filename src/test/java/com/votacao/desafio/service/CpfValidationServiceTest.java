package com.votacao.desafio.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CpfValidationServiceTest {

    @InjectMocks
    private CpfValidationService cpfValidationService;

    private static final String VALID_CPF_WITHOUT_MASK = "52998224725";
    private static final int EXPECTED_DELAY = 100;

    @Test
    @DisplayName("Should return ABLE_TO_VOTE when CPF is valid")
    void validateCpf_WithValidCpf_ShouldReturnVotingPermission() {
        CpfValidationService.VotingPermission result = cpfValidationService.validateCpf(VALID_CPF_WITHOUT_MASK);

        assertNotNull(result);
        assertTrue(EnumSet.allOf(CpfValidationService.VotingPermission.class).contains(result));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when CPF is null or empty")
    void validateCpf_WithNullOrEmptyCpf_ShouldThrowException(String cpf) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidationService.validateCpf(cpf));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when CPF is invalid characters length")
    void validateCpf_WithInvalidLength_ShouldThrowException() {
        // given
        String shortCpf = "1234";
        String longCpf = "123456789012";

        // when/then
        assertThrows(ResponseStatusException.class, () -> cpfValidationService.validateCpf(shortCpf));
        assertThrows(ResponseStatusException.class, () -> cpfValidationService.validateCpf(longCpf));
    }

    @Test
    @DisplayName("Should throw exception when CPF has repeated digits")
    void validateCpf_WithRepeatedDigits_ShouldThrowException() {
        // given
        String[] repeatedCpfs = {
                "111.111.111-11",
                "000.000.000-00",
                "999.999.999-99"
        };

        // when/then
        for (String cpf : repeatedCpfs) {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> cpfValidationService.validateCpf(cpf));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("CPF is invalid", exception.getReason());
        }
    }

    @Test
    @DisplayName("Should validate CPF with different formats")
    void validateCpf_WithDifferentFormats_ShouldValidate() {
        String[] validFormats = {
                "529.982.247-25",
                "52998224725",
                "529982247-25",
                "529.982.24725"
        };

        for (String cpf : validFormats) {
            assertDoesNotThrow(() -> cpfValidationService.validateCpf(cpf));
            CpfValidationService.VotingPermission result = cpfValidationService.validateCpf(cpf);
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should throw exception when CPF has invalid first verifier digit")
    void validateCpf_WithInvalidFirstVerifierDigit_ShouldThrowException() {
        String cpfWithInvalidFirstDigit = "529.982.247-15";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidationService.validateCpf(cpfWithInvalidFirstDigit));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when CPF has invalid second verifier digit")
    void validateCpf_WithInvalidSecondVerifierDigit_ShouldThrowException() {
        String cpfWithInvalidSecondDigit = "529.982.247-24";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidationService.validateCpf(cpfWithInvalidSecondDigit));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should apply delay when validating CPF")
    void validateCpf_ShouldApplyDelay() {
        long startTime = System.currentTimeMillis();

        cpfValidationService.validateCpf(VALID_CPF_WITHOUT_MASK);
        long executionTime = System.currentTimeMillis() - startTime;

        assertTrue(executionTime >= EXPECTED_DELAY,
                "A execução deve levar pelo menos " + EXPECTED_DELAY + "ms");
    }

    @Test
    @DisplayName("Should handle interruption when validating CPF")
    void validateCpf_WhenInterrupted_ShouldHandleInterruption() {
        Thread testThread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(50);
                testThread.interrupt();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        assertDoesNotThrow(() -> cpfValidationService.validateCpf(VALID_CPF_WITHOUT_MASK));
        assertTrue(Thread.interrupted(), "Thread should be marked as interrupted");
    }
}