package com.votacao.desafio.service;

import com.votacao.desafio.common.client.CpfValidatorClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CpfValidationClientTest {

    private CpfValidatorClient cpfValidatorClient;
    private Random mockRandom;

    private static final String VALID_CPF_WITHOUT_MASK = "52998224725";

    @BeforeEach
    void setup() {
        mockRandom = mock(Random.class);
        cpfValidatorClient = new CpfValidatorClient(mockRandom);
    }

    @Test
    @DisplayName("Should return ABLE_TO_VOTE when random returns true")
    void validateCpf_WhenRandomReturnsTrue_ShouldReturnAbleToVote() {
        when(mockRandom.nextBoolean()).thenReturn(true);

        CpfValidatorClient.VotingPermission result = cpfValidatorClient.validateCpf(VALID_CPF_WITHOUT_MASK);

        assertEquals(CpfValidatorClient.VotingPermission.ABLE_TO_VOTE, result);
    }

    @Test
    @DisplayName("Should throw exception when random returns false (UNABLE_TO_VOTE)")
    void validateCpf_WhenRandomReturnsFalse_ShouldThrowException() {
        when(mockRandom.nextBoolean()).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> cpfValidatorClient.validateCpf(VALID_CPF_WITHOUT_MASK)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when CPF is invalid characters length")
    void validateCpf_WithInvalidLength_ShouldThrowException() {
        String shortCpf = "1234";
        String longCpf = "123456789012";

        assertThrows(ResponseStatusException.class, () -> cpfValidatorClient.validateCpf(shortCpf));
        assertThrows(ResponseStatusException.class, () -> cpfValidatorClient.validateCpf(longCpf));
    }

    @Test
    @DisplayName("Should throw exception when CPF has repeated digits")
    void validateCpf_WithRepeatedDigits_ShouldThrowException() {
        String[] repeatedCpfs = {
                "11111111111",
                "00000000000",
                "99999999999"
        };

        for (String cpf : repeatedCpfs) {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> cpfValidatorClient.validateCpf(cpf));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("CPF is invalid", exception.getReason());
        }
    }

    @Test
    @DisplayName("Should throw exception when CPF has invalid first verifier digit")
    void validateCpf_WithInvalidFirstVerifierDigit_ShouldThrowException() {
        String cpfWithInvalidFirstDigit = "02998224715";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidatorClient.validateCpf(cpfWithInvalidFirstDigit));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when CPF has invalid second verifier digit")
    void validateCpf_WithInvalidSecondVerifierDigit_ShouldThrowException() {
        String cpfWithInvalidSecondDigit = "52998224724";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidatorClient.validateCpf(cpfWithInvalidSecondDigit));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when CPF is null or empty")
    void validateCpf_WithNullOrEmptyCpf_ShouldThrowException(String cpf) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cpfValidatorClient.validateCpf(cpf));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CPF is invalid", exception.getReason());
    }

    @Test
    @DisplayName("Should apply delay when validating CPF")
    void validateCpf_ShouldApplyDelay() {
        when(mockRandom.nextBoolean()).thenReturn(true);

        long startTime = System.currentTimeMillis();

        cpfValidatorClient.validateCpf(VALID_CPF_WITHOUT_MASK);
        long executionTime = System.currentTimeMillis() - startTime;

        assertTrue(executionTime >= 100,
                String.format("A execução deve levar pelo menos %dms, mas levou %dms",
                        100, executionTime));
    }
}