package com.votacao.desafio.swagger;

import com.votacao.desafio.dto.VotingSessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Sessões de Votação", description = "API para gerenciamento de sessões de votação")
public interface VotingSessionSwagger {

    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    value = "{" +
                                            "    \"path\": \"/api/v1/votacao/pautas\"," +
                                            "    \"message\": \"Invalid Request\"," +
                                            "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                            "    \"status\": 400" +
                                            "}")
                    }
            )
    )

    @Operation(
            summary = "Listar sessões de votação",
            description = "Retorna uma lista paginada de todas as sessões de votação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    @GetMapping
    ResponseEntity<Page<VotingSessionResponse>> listAllVotingSessions(
            @Parameter(description = "Status da sessão (OPEN/CLOSED)", example = "OPEN")
            @RequestParam(required = false) String votingSessionStatus,
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam Integer page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam Integer size
    );

    @Operation(
            summary = "Abrir sessão de votação",
            description = "Abre uma nova sessão de votação para uma pauta específica"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão de votação aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/sessoes\"," +
                                                    "    \"message\": \"Invalid Request\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 400" +
                                                    "}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/pautas/1\"," +
                                                    "    \"message\": \"Pauta not found with id 1\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 404" +
                                                    "}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Já existe uma sessão de votação aberta para esta pauta",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/sessoes\"," +
                                                    "    \"message\": \"Voting session already open for this pauta\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 409" +
                                                    "}")
                            }
                    )

            )
    })
    @PostMapping("/pauta/{pautaId}")
    ResponseEntity<VotingSessionResponse> openVotingSessionByPautaId(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long pautaId,
            @Parameter(description = "Duração da sessão em minutos", example = "1")
            @RequestParam(defaultValue = "1") Integer votingSessionDurationInMinutes
    );

    @Operation(
            summary = "Buscar sessão de votação",
            description = "Retorna os detalhes de uma sessão de votação específica"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão de votação encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão de votação não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/sessoes/1\"," +
                                                    "    \"message\": \"Voting session not found with id 1\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 404" +
                                                    "}")
                            }
                    )
            )
    })
    @GetMapping("/{sessionId}")
    ResponseEntity<VotingSessionResponse> getVotingSessionById(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long sessionId
    );
}