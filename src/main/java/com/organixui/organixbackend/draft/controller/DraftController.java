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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de rascunhos.
 * Inclui operações CRUD com controle de acesso baseado em roles.
 */
@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
@Tag(name = "Drafts", description = "Draft management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DraftController {
    
    private final DraftService draftService;
    
    @GetMapping
    @Operation(summary = "Listar rascunhos", description = "Lista rascunhos do usuário ou todos (se ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de rascunhos retornada com sucesso")
    })
    public ResponseEntity<List<DraftResponse>> getAllDrafts(
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) String status,
            @Parameter(description = "Filtrar por produto") @RequestParam(required = false) UUID productId) {
        List<DraftResponse> drafts = draftService.getAllDrafts(status, productId);
        return ResponseEntity.ok(drafts);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar rascunho por ID", description = "Busca um rascunho específico por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho encontrado"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado")
    })
    public ResponseEntity<DraftResponse> getDraftById(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id) {
        DraftResponse draft = draftService.getDraftById(id);
        return ResponseEntity.ok(draft);
    }
    
    @PostMapping
    @Operation(summary = "Criar rascunho", description = "Cria um novo rascunho")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rascunho criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DraftResponse> createDraft(
            @Parameter(description = "Dados do novo rascunho") @Valid @RequestBody CreateDraftRequest request) {
        DraftResponse draft = draftService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar rascunho", description = "Atualiza os dados de um rascunho existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este rascunho")
    })
    public ResponseEntity<DraftResponse> updateDraft(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id,
            @Parameter(description = "Novos dados do rascunho") @Valid @RequestBody UpdateDraftRequest request) {
        DraftResponse draft = draftService.updateDraft(id, request);
        return ResponseEntity.ok(draft);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir rascunho", description = "Exclui um rascunho")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rascunho excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para excluir este rascunho")
    })
    public ResponseEntity<Void> deleteDraft(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id) {
        draftService.deleteDraft(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar status do rascunho", description = "Altera o status de um rascunho (apenas administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<DraftResponse> updateDraftStatus(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id,
            @Parameter(description = "Novo status") @RequestParam String status) {
        DraftResponse draft = draftService.updateDraftStatus(id, status);
        return ResponseEntity.ok(draft);
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprovar rascunho", description = "Aprova um rascunho para conversão em conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rascunho aprovado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<DraftResponse> approveDraft(
            @Parameter(description = "ID do rascunho") @PathVariable UUID id) {
        DraftResponse draft = draftService.approveDraft(id);
        return ResponseEntity.ok(draft);
    }
}
