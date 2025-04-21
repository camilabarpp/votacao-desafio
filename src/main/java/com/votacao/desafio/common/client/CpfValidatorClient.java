package com.votacao.desafio.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpfValidatorClient {

    private final Random random;

    public CpfValidatorClient() {
        this(new Random());
    }

    public enum VotingPermission {
        ABLE_TO_VOTE,
        UNABLE_TO_VOTE
    }

    private static final int DELAY_SIMULACAO = 100;

    /**
     * Simula integração com API externa de validação de CPF.
     * IMPORTANTE: Implementação fake que retorna resultados aleatórios para CPFs válidos.
     *
     * @param cpf CPF a ser validado
     * @return Status da votação (ABLE_TO_VOTE ou UNABLE_TO_VOTE)
     * @throws ResponseStatusException (400) se o CPF for inválido
     */
    public VotingPermission validateCpf(String cpf) {
        log.info("Verifying CPF: {}", cpf);

        if (!isValidCpf(cpf)) {
            log.error("Invalid CPF: {}", cpf);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF is invalid");
        }

        delayForTesting();

        VotingPermission status = random.nextBoolean()
                ? VotingPermission.ABLE_TO_VOTE
                : VotingPermission.UNABLE_TO_VOTE;

        if (VotingPermission.UNABLE_TO_VOTE.equals(status)) {
            log.error("Invalid CPF: {}", cpf);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF is invalid");
        }

        log.info("CPF analyzed: {} - Status: {}", cpf, status);
        return status;
    }

    private void delayForTesting() {
        try {
            Thread.sleep(DELAY_SIMULACAO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int fisrtCheckDigit = 11 - (sum % 11);
        if (fisrtCheckDigit > 9) {
            fisrtCheckDigit = 0;
        }

        if (Character.getNumericValue(cpf.charAt(9)) != fisrtCheckDigit) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondCheckDigit = 11 - (sum % 11);
        if (secondCheckDigit > 9) {
            secondCheckDigit = 0;
        }

        return Character.getNumericValue(cpf.charAt(10)) == secondCheckDigit;
    }
}