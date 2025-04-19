package com.votacao.desafio.swagger;

import com.votacao.desafio.dto.AssociateRequest;
import com.votacao.desafio.dto.AssociateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Associados", description = "API para gerenciamento de associados")
public interface AssociateSwagger {

    @Operation(summary = "Listar todos os associados", description = "Retorna uma lista paginada de todos os associados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de associados retornada com sucesso")
    })
    @GetMapping
    ResponseEntity<Page<AssociateResponse>> getAllAssociates(
            @Parameter(description = "Número da página (começa em 0)", example = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10") int size);

    @Operation(summary = "Buscar associado por ID", description = "Retorna um associado específico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Associado encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/associates/123\"," +
                                                    "    \"message\": \"Associate not found with ID: 123\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 404" +
                                                    "}")
                            }
                    )
            )
    })
    @GetMapping("/{associateId}")
    ResponseEntity<AssociateResponse> getAssociateById(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long associateId);

    @Operation(summary = "Criar novo associado", description = "Cria um novo associado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Associado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/associates\"," +
                                                    "    \"message\": \"Invalid request\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 400" +
                                                    "}")
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<AssociateResponse> createAssociate(@RequestBody AssociateRequest associateRequest);

    @Operation(summary = "Atualizar associado", description = "Atualiza os dados de um associado existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Associado atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/associates/123\"," +
                                                    "    \"message\": \"Associate not found with ID: 123\"," +
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
                                                    "    \"path\": \"/api/v1/votacao/associates/123\"," +
                                                    "    \"message\": \"Invalid request\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 400" +
                                                    "}")
                            }
                    )
            )
    })
    @PutMapping("/{associateId}")
    ResponseEntity<AssociateResponse> updateAssociate(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long associateId,
            @RequestBody AssociateRequest associateRequest);

    @Operation(summary = "Excluir associado", description = "Exclui um associado existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Associado excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{" +
                                                    "    \"path\": \"/api/v1/votacao/associates/123\"," +
                                                    "    \"message\": \"Associate not found with ID: 123\"," +
                                                    "    \"timestamp\": \"2025-04-19T10:57:53.030701439\"," +
                                                    "    \"status\": 404" +
                                                    "}")
                            }
                    )
            )
    })
    @DeleteMapping("/{associateId}")
    ResponseEntity<Void> deleteAssociate(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long associateId);
}
