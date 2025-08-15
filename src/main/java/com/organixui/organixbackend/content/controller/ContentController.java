package com.organixui.organixbackend.content.controller;

import com.organixui.organixbackend.content.dto.ContentRequest;
import com.organixui.organixbackend.content.dto.ContentResponse;
import com.organixui.organixbackend.content.dto.UpdateContentRequest;
import com.organixui.organixbackend.content.dto.TransformDraftRequest;
import com.organixui.organixbackend.content.dto.UpdateContentStatusRequest;
import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.service.ContentService;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.dto.ChannelMetricResponse;
import com.organixui.organixbackend.performance.dto.UpdateContentMetricsRequest;
import com.organixui.organixbackend.performance.dto.UpdateChannelMetricRequest;
import com.organixui.organixbackend.performance.dto.UpdateContentChannelMetricsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de conteúdo.
 * ADMIN pode acessar todo conteúdo, OPERATOR apenas o próprio.
 */
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Content management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ContentController {
    
    private final ContentService contentService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Listar conteúdo", description = "Lista conteúdo da empresa com filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de conteúdo retornada com sucesso")
    })
    public ResponseEntity<List<ContentResponse>> getAllContent(
            @Parameter(description = "Filtro por status do conteúdo")
            @RequestParam(required = false) ContentStatus status,
            @Parameter(description = "Filtro por canal específico (ID do canal)")
            @RequestParam(required = false) UUID channelId,
            @Parameter(description = "Filtro por produto específico")
            @RequestParam(required = false) UUID productId,
            @Parameter(description = "Filtro por usuário específico (creator ou producer)")
            @RequestParam(required = false) UUID userId) {
        List<ContentResponse> content = contentService.getAllContent(status, channelId, productId, userId);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Listar meu conteúdo", description = "Lista conteúdo do usuário atual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de conteúdo retornada com sucesso")
    })
    public ResponseEntity<List<ContentResponse>> getMyContent() {
        List<ContentResponse> content = contentService.getMyContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Obter conteúdo", description = "Obtém um conteúdo específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo encontrado"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> getContentById(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id) {
        ContentResponse content = contentService.getContentById(id);
        return ResponseEntity.ok(content);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Criar conteúdo", description = "Cria um novo conteúdo com status inicial customizável. Se não especificado, o status será PENDING")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ContentResponse> createContent(
            @Parameter(description = "Dados do conteúdo (status opcional, padrão: PENDING)") @Valid @RequestBody ContentRequest request) {
        ContentResponse content = contentService.createContent(request);
        return ResponseEntity.ok(content);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar conteúdo", description = "Atualiza um conteúdo existente, incluindo status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> updateContent(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id,
            @Parameter(description = "Dados para atualização") @Valid @RequestBody UpdateContentRequest request) {
        ContentResponse content = contentService.updateContent(id, request);
        return ResponseEntity.ok(content);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Excluir conteúdo", description = "Exclui um conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conteúdo excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> deleteContent(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/transform-draft/{draftId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Transformar rascunho em conteúdo", description = "Transforma um rascunho aprovado em conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo criado com sucesso a partir do rascunho"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "400", description = "Rascunho não está aprovado ou dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> transformDraftToContent(
            @Parameter(description = "ID do rascunho") @PathVariable UUID draftId,
            @Parameter(description = "Dados adicionais para o conteúdo") @Valid @RequestBody TransformDraftRequest request) {
        ContentResponse content = contentService.transformDraftToContent(draftId, request);
        return ResponseEntity.ok(content);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar status do conteúdo", description = "Atualiza apenas o status de um conteúdo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Status inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> updateContentStatus(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id,
            @Parameter(description = "Novo status do conteúdo") @Valid @RequestBody UpdateContentStatusRequest request) {
        ContentResponse content = contentService.updateContentStatus(id, request);
        return ResponseEntity.ok(content);
    }
    
    @PutMapping("/{id}/metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar métricas do conteúdo", description = "Atualiza as métricas totais de um conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentMetricsResponse> updateContentMetrics(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id,
            @Parameter(description = "Dados das métricas") @Valid @RequestBody UpdateContentMetricsRequest request) {
        ContentMetricsResponse metrics = contentService.updateContentMetrics(id, request);
        return ResponseEntity.ok(metrics);
    }
    
    @PutMapping("/{contentId}/channels/{channelId}/metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar métricas do canal", description = "Atualiza as métricas de um canal específico para um conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas do canal atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo ou canal não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ChannelMetricResponse> updateChannelMetrics(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID contentId,
            @Parameter(description = "ID do canal") @PathVariable UUID channelId,
            @Parameter(description = "Dados das métricas do canal") @Valid @RequestBody UpdateChannelMetricRequest request) {
        ChannelMetricResponse metrics = contentService.updateChannelMetrics(contentId, channelId, request);
        return ResponseEntity.ok(metrics);
    }
    
    @PutMapping("/{id}/channels/metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar métricas de todos os canais", description = "Atualiza as métricas de todos os canais de um conteúdo de uma vez")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas dos canais atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentMetricsResponse> updateContentChannelMetrics(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id,
            @Parameter(description = "Dados das métricas de todos os canais") @Valid @RequestBody UpdateContentChannelMetricsRequest request) {
        ContentMetricsResponse metrics = contentService.updateContentChannelMetrics(id, request);
        return ResponseEntity.ok(metrics);
    }
}
