package com.votacao.desafio.dto;

import lombok.Getter;

@Getter
public class VoteRequest {
    private Long sessionId;
    private String idAssociated;
    private boolean vote;
}