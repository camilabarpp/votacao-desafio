package com.votacao.desafio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CpfValidationService {

    public enum StatusVotacao {
        ABLE_TO_VOTE,
        UNABLE_TO_VOTE
    }

    public StatusVotacao validateCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return StatusVotacao.UNABLE_TO_VOTE;
        }

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return StatusVotacao.UNABLE_TO_VOTE;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return StatusVotacao.UNABLE_TO_VOTE;
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
            return StatusVotacao.UNABLE_TO_VOTE;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito > 9) {
            segundoDigito = 0;
        }

        if (Character.getNumericValue(cpf.charAt(10)) != segundoDigito) {
            return StatusVotacao.UNABLE_TO_VOTE;
        }

        return StatusVotacao.ABLE_TO_VOTE;
    }
}