package com.votacao.desafio.common.swagger;

import com.votacao.desafio.dto.VoteRequest;
import com.votacao.desafio.dto.VotingResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Votos", description = "API para gerenciamento de votos")
public interface VoteSwagger {

    @Operation(
            summary = "Registrar voto",
            description = "Registra o voto de um associado em uma pauta específica"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou sessão de votação fechada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "    \"path\": \"/api/v1/votacao/sessoes\"," +
                                            "    \"message\": \"Vote is required\"," +
                                            "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                            "    \"status\": 400" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "    \"path\": \"/api/v1/votacao/sessoes/pauta/123\"," +
                                            "    \"message\": \"Pauta not found with ID:  123\"," +
                                            "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                            "    \"status\": 404" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta pauta",
                    content = @Content
                            (mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "    \"path\": \"/api/v1/votacao/sessoes/pauta/123\"," +
                                            "    \"message\": \"Associate already voted in this session\"," +
                                            "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                            "    \"status\": 409" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/pauta/{pautaId}")
    ResponseEntity<VotingResultResponse> registerVote(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long pautaId,
            @RequestBody @Valid VoteRequest voteRequest
    );
}