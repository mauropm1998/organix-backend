package com.organixui.organixbackend.draft.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.organixui.organixbackend.draft.dto.CreateDraftRequest;
import com.organixui.organixbackend.draft.dto.DraftResponse;
import com.organixui.organixbackend.draft.dto.DraftStatsResponse;
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
    public ResponseEntity<?> getAllDrafts(
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) String status,
            @Parameter(description = "Filtrar por criador") @RequestParam(required = false) UUID creatorId,
            @Parameter(description = "Número da página (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Tamanho da página") @RequestParam(required = false) Integer size) {
        if (page != null || size != null) {
            int p = page != null ? page : 0;
            int s = size != null ? size : 20;
            var pageable = org.springframework.data.domain.PageRequest.of(p, s);
            return ResponseEntity.ok(draftService.getAllDrafts(status, creatorId, pageable));
        }
        return ResponseEntity.ok(draftService.getAllDrafts(status));
    }

    @GetMapping("/recent")
    @Operation(summary = "Rascunhos recentes", description = "Lista rascunhos criados nos últimos N dias (padrão 7)")
    public ResponseEntity<?> getRecentDrafts(
            @Parameter(description = "Quantidade de dias para trás") @RequestParam(required = false) Integer days) {
        int d = (days == null || days <= 0) ? 7 : Math.min(days, 30);
        return ResponseEntity.ok(draftService.getRecentDrafts(d));
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

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas de rascunhos", description = "Retorna total de rascunhos e total aprovados (APPROVED)")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DraftStatsResponse.class),
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "draftStatsExample",
                summary = "Exemplo de estatísticas de rascunhos",
                value = "{\n  'total': 54,\n  'approved': 18\n}")))
    })
    public ResponseEntity<DraftStatsResponse> getDraftStats() {
        return ResponseEntity.ok(draftService.getDraftStats());
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
