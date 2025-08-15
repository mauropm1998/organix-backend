package com.organixui.organixbackend.draft.controller;

import com.organixui.organixbackend.draft.dto.CreateDraftRequest;
import com.organixui.organixbackend.draft.dto.DraftResponse;
import com.organixui.organixbackend.draft.dto.UpdateDraftRequest;
import com.organixui.organixbackend.draft.service.DraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de rascunhos.
 * ADMIN pode acessar todos os rascunhos, OPERATOR apenas os próprios.
 */
@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
@Tag(name = "Drafts", description = "Draft management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DraftController {
    
    private final DraftService draftService;
    
    @GetMapping
    @Operation(summary = "Listar rascunhos", description = "Lista rascunhos com filtros por status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de rascunhos retornada com sucesso")
    })
    public ResponseEntity<List<DraftResponse>> getAllDrafts(
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) String status) {
        List<DraftResponse> drafts = draftService.getAllDrafts(status);
        return ResponseEntity.ok(drafts);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter rascunho", description = "Obtém um rascunho específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho encontrado"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<DraftResponse> getDraftById(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id) {
        DraftResponse draft = draftService.getDraftById(id);
        return ResponseEntity.ok(draft);
    }
    
    @PostMapping
    @Operation(summary = "Criar rascunho", description = "Cria um novo rascunho. O status pode ser informado ou será PENDING por padrão")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DraftResponse> createDraft(
            @Parameter(description = "Dados do rascunho") @Valid @RequestBody CreateDraftRequest request) {
        DraftResponse draft = draftService.createDraft(request);
        return ResponseEntity.ok(draft);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar rascunho", description = "Atualiza um rascunho existente, incluindo seu status (PENDING, APPROVED, NOT_APPROVED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<DraftResponse> updateDraft(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id,
            @Parameter(description = "Dados para atualização") @Valid @RequestBody UpdateDraftRequest request) {
        DraftResponse draft = draftService.updateDraft(id, request);
        return ResponseEntity.ok(draft);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir rascunho", description = "Exclui um rascunho")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rascunho excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> deleteDraft(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id) {
        draftService.deleteDraft(id);
        return ResponseEntity.noContent().build();
    }
}
