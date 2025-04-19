package com.votacao.desafio.common.swagger;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.dto.PautaResponse;
import com.votacao.desafio.dto.VotingResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pautas", description = "API para gerenciamento de pautas")
public interface PautaSwagger {

    @Operation(summary = "Criar nova pauta", description = "Cria uma nova pauta para votação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
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
    })
    @PostMapping
    ResponseEntity<PautaResponse> createPauta(@RequestBody @Valid PautaRequest pautaRequest);

    @Operation(summary = "Listar todas as pautas", description = "Retorna uma lista paginada de todas as pautas, com opção de filtro por status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso")
    })
    @GetMapping
    ResponseEntity<Page<PautaResponse>> listAllPautas(
            @Parameter(description = "Status da pauta", example = "OPEN")
            @RequestParam(required = false) String status,
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam int size);

    @Operation(summary = "Buscar pauta por ID", description = "Retorna uma pauta específica pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta encontrada com sucesso"),
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
            )
    })
    @GetMapping("/{pautaId}")
    ResponseEntity<PautaResponse> getPautaById(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long pautaId);

    @Operation(summary = "Obter resultado da votação", description = "Retorna o resultado da votação de uma pauta específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso"),
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
    })
    @GetMapping("/{pautaId}/resultado")
    ResponseEntity<VotingResultResponse> getVotingResult(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long pautaId);
}