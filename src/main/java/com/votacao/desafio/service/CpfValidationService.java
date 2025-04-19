package com.votacao.desafio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@Slf4j
@Service
public class CpfValidationService {

    private static final int DELAY_SIMULACAO = 100;

    public enum VotingPermission {
        ABLE_TO_VOTE,
        UNABLE_TO_VOTE
    }

    /**
     * Simula integração com API externa de validação de CPF.
     * IMPORTANTE: Implementação fake que retorna resultados aleatórios para CPFs válidos.
     *
     * @param cpf CPF a ser validado
     * @return Status da votação (ABLE_TO_VOTE ou UNABLE_TO_VOTE)
     * @throws ResponseStatusException (400) se o CPF for inválido
     */
    public VotingPermission validateCpf(String cpf) {
        log.info("Validando CPF: {}", cpf);

        delayForTesting();

        if (!isValidCpf(cpf)) {
            log.error("CPF inválido: {}", cpf);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF is invalid");
        }

        VotingPermission status = new Random().nextBoolean()
                ? VotingPermission.ABLE_TO_VOTE
                : VotingPermission.UNABLE_TO_VOTE;

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

    private boolean isValidCpf(String cpf) {
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

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito > 9) {
            primeiroDigito = 0;
        }

        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito > 9) {
            segundoDigito = 0;
        }

        return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }
}