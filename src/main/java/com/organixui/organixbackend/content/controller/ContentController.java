package com.organixui.organixbackend.content.controller;

import com.organixui.organixbackend.content.dto.ContentRequest;
import com.organixui.organixbackend.content.dto.ContentResponse;
import com.organixui.organixbackend.content.history.dto.ContentStatusHistoryResponse;
import com.organixui.organixbackend.content.history.repository.ContentStatusHistoryRepository;
import com.organixui.organixbackend.content.history.model.ContentStatusHistory;
import com.organixui.organixbackend.user.repository.UserRepository;
import com.organixui.organixbackend.content.dto.UpdateContentRequest;
import com.organixui.organixbackend.content.dto.TransformDraftRequest;
import com.organixui.organixbackend.content.dto.UpdateContentStatusRequest;
import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.model.TrafficType;
import com.organixui.organixbackend.content.service.ContentService;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.dto.ChannelMetricResponse;
import com.organixui.organixbackend.performance.dto.UpdateContentMetricsRequest;
import com.organixui.organixbackend.performance.dto.UpdateChannelMetricRequest;
import com.organixui.organixbackend.performance.dto.UpdateContentChannelMetricsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private final ContentStatusHistoryRepository contentStatusHistoryRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Listar conteúdo", 
        description = "Lista conteúdo da empresa com filtros opcionais, ordenado por creationDate desc. Suporta paginação (?page=&size=). " +
            "Permite filtrar por status, canal, produto, usuário e tipo de tráfego (PAID ou ORGANIC).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo retornado com sucesso",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = ContentResponse.class)),
                    examples = @ExampleObject(name = "contentListWithTraffic",
                        summary = "Exemplo de listagem com tipo de tráfego",
                        value = "[\n  { \"id\": \"3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e\", \"name\": \"Campanha Paga\", \"type\": \"SOCIAL_POST\", \"trafficType\": \"PAID\", \"content\": \"Post pago...\", \"productId\": \"11111111-2222-3333-4444-555555555555\", \"creatorId\": \"aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\", \"creatorName\": \"Maria Silva\", \"creationDate\": \"2025-09-22T10:05:00\", \"status\": \"POSTED\", \"companyId\": \"99999999-8888-7777-6666-555555555555\" },\n  { \"id\": \"4c9e3b2d-8f5g-5g3b-0d7e-2f3c5a6d7e8f\", \"name\": \"Post Orgânico\", \"type\": \"SOCIAL_POST\", \"trafficType\": \"ORGANIC\", \"content\": \"Post orgânico...\", \"productId\": \"11111111-2222-3333-4444-555555555555\", \"creatorId\": \"bbbbbbbb-cccc-dddd-eeee-ffffffffffff\", \"creatorName\": \"João Silva\", \"creationDate\": \"2025-09-20T14:30:00\", \"status\": \"POSTED\", \"companyId\": \"99999999-8888-7777-6666-555555555555\" }\n]")),
                @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "contentPage",
                        summary = "Exemplo de listagem paginada (Page)",
                        value = "{\n  \"content\": [\n    { \"id\": \"3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e\", \"name\": \"Post Primavera\", \"type\": \"SOCIAL_POST\", \"trafficType\": \"PAID\", \"content\": \"Texto...\", \"productId\": \"11111111-2222-3333-4444-555555555555\", \"creatorId\": \"aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\", \"creatorName\": \"Maria Silva\", \"creationDate\": \"2025-09-22T10:05:00\", \"status\": \"IN_PRODUCTION\", \"companyId\": \"99999999-8888-7777-6666-555555555555\" }\n  ],\n  \"pageable\": { \"pageNumber\": 0, \"pageSize\": 20, \"sort\": { \"sorted\": true, \"unsorted\": false, \"empty\": false } },\n  \"totalElements\": 128,\n  \"totalPages\": 7,\n  \"size\": 20,\n  \"number\": 0,\n  \"sort\": { \"sorted\": true, \"unsorted\": false, \"empty\": false },\n  \"first\": true,\n  \"last\": false,\n  \"numberOfElements\": 20,\n  \"empty\": false\n}"))
            }
        )
    })
    public ResponseEntity<?> getAllContent(
        @Parameter(description = "Filtro por status do conteúdo",
            schema = @Schema(allowableValues = {"PENDING","IN_PRODUCTION","POSTED","PRODUCTION_FINISHED","FINISHED","CANCELED"}))
        @RequestParam(required = false) ContentStatus status,
            @Parameter(description = "Filtro por canal específico (ID do canal)") @RequestParam(required = false) UUID channelId,
            @Parameter(description = "Filtro por produto específico", example = "11111111-2222-3333-4444-555555555555")
            @RequestParam(required = false) UUID productId,
            @Parameter(description = "Filtro por usuário específico (creator ou producer)") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Filtro por tipo de tráfego", 
                schema = @Schema(allowableValues = {"PAID", "ORGANIC"}),
                example = "PAID")
            @RequestParam(required = false) TrafficType trafficType,
            @Parameter(description = "Número da página (0-based)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "Tamanho da página", example = "20") @RequestParam(required = false) Integer size) {
        if (page != null || size != null) {
            int p = page != null ? page : 0;
            int s = size != null ? size : 20;
            var pageable = org.springframework.data.domain.PageRequest.of(p, s);
            return ResponseEntity.ok(contentService.getAllContent(status, channelId, productId, userId, trafficType, pageable));
        }
        return ResponseEntity.ok(contentService.getAllContent(status, channelId, productId, userId, trafficType));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Conteúdos recentes", description = "Lista conteúdos criados nos últimos N dias (padrão 7)")
    public ResponseEntity<List<ContentResponse>> getRecentContent(
            @Parameter(description = "Quantidade de dias para trás") @RequestParam(required = false) Integer days) {
        int d = (days == null || days <= 0) ? 7 : Math.min(days, 30);
        return ResponseEntity.ok(contentService.getRecentContent(d));
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

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Estatísticas de conteúdo", description = "Retorna total de conteúdo e total em produção (IN_PRODUCTION)")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.organixui.organixbackend.content.dto.ContentStatsResponse.class),
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "contentStatsExample",
                summary = "Exemplo de estatísticas de conteúdo",
                value = "{\n  'total': 128,\n  'inProduction': 37\n}")))
    })
    public ResponseEntity<com.organixui.organixbackend.content.dto.ContentStatsResponse> getContentStats() {
        return ResponseEntity.ok(contentService.getContentStats());
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

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Histórico de status", description = "Lista o histórico de mudanças de status do conteúdo, incluindo status anterior e novo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico retornado"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<ContentStatusHistoryResponse>> getHistory(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id) {
        List<ContentStatusHistory> history = contentStatusHistoryRepository.findByContentIdOrderByChangedAtAsc(id);
        List<ContentStatusHistoryResponse> response = history.stream().map(h -> {
            String userName = userRepository.findById(h.getUserId()).map(u -> u.getName()).orElse(null);
            return ContentStatusHistoryResponse.builder()
                    .id(h.getId())
                    .contentId(id)
                    .userId(h.getUserId())
                    .userName(userName)
                    .newStatus(h.getNewStatus())
                    .changedAt(h.getChangedAt())
                    .build();
        }).toList();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Criar conteúdo", 
        description = "Cria um novo conteúdo com status inicial customizável (PENDING por padrão). " +
            "Suporta configuração de tipo de tráfego (PAID ou ORGANIC) para melhor segmentação de análises.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo criado com sucesso",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ContentResponse.class),
                        examples = @ExampleObject(name = "contentCreatedWithTraffic",
                                summary = "Exemplo de conteúdo pago criado",
                                value = "{\n  'id': '3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e',\n  'name': 'Campanha Paga Primavera',\n  'type': 'SOCIAL_POST',\n  'trafficType': 'PAID',\n  'content': 'Texto do post pago...',\n  'productId': '11111111-2222-3333-4444-555555555555',\n  'creatorId': 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',\n  'creatorName': 'Maria Silva',\n  'creationDate': '2025-08-25T09:05:12',\n  'postDate': '2025-09-01T10:30:00',\n  'productionStartDate': '2025-08-25T09:00:00',\n  'productionEndDate': '2025-08-27T18:45:00',\n  'metaAdsId': '123456789012345',\n  'producerId': 'bbbbbbbb-cccc-dddd-eeee-ffffffffffff',\n  'producerName': 'João Almeida',\n  'status': 'PENDING',\n  'channels': [],\n  'companyId': '99999999-8888-7777-6666-555555555555',\n  'metrics': null\n}"))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou tipo de tráfego inválido (deve ser PAID ou ORGANIC)")
    })
    public ResponseEntity<ContentResponse> createContent(
            @Parameter(description = "Dados do conteúdo (trafficType e status opcionais)") @Valid @RequestBody ContentRequest request) {
        ContentResponse content = contentService.createContent(request);
        return ResponseEntity.ok(content);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Atualizar conteúdo", 
        description = "Atualiza um conteúdo existente, incluindo status e tipo de tráfego. " +
            "Permite alterar de PAID para ORGANIC ou vice-versa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo atualizado com sucesso",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ContentResponse.class),
                        examples = @ExampleObject(name = "contentUpdated",
                                summary = "Exemplo de conteúdo atualizado",
                                value = "{\n  'id': '3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e',\n  'name': 'Post de Campanha Primavera (Revisto)',\n  'type': 'SOCIAL_POST',\n  'trafficType': 'ORGANIC',\n  'content': 'Texto revisado...',\n  'productId': '11111111-2222-3333-4444-555555555555',\n  'creatorId': 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',\n  'creatorName': 'Maria Silva',\n  'creationDate': '2025-08-25T09:05:12',\n  'postDate': '2025-09-01T10:30:00',\n  'status': 'POSTED',\n  'channels': [],\n  'companyId': '99999999-8888-7777-6666-555555555555'\n}"))),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou tipo de tráfego inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> updateContent(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID id,
            @Parameter(description = "Dados para atualização (qualquer campo é opcional)") @Valid @RequestBody UpdateContentRequest request) {
        ContentResponse content = contentService.updateContent(id, request);
        return ResponseEntity.ok(content);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Excluir conteúdo", 
        description = "Exclui um conteúdo de forma completa. Remove também métricas, histórico de status, audit logs e associações com canais. " +
            "Apenas ADMIN ou o criador do conteúdo podem deletar.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conteúdo excluído com sucesso (inclusive métricas, histórico e auditoria)"),
        @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado (apenas ADMIN ou criador)")
    })
    public ResponseEntity<Void> deleteContent(
            @Parameter(description = "ID do conteúdo a ser deletado", example = "3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e") @PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/transform-draft/{draftId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Transformar rascunho em conteúdo", description = "Transforma um rascunho aprovado em conteúdo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteúdo criado com sucesso a partir do rascunho",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ContentResponse.class),
                examples = @ExampleObject(name = "transformDraftResponse",
                    summary = "Exemplo de resposta ao transformar rascunho",
                    value = "{\n  'id': '7d8e9f10-1112-1314-1516-171819202122',\n  'name': 'Landing Page Promo Setembro',\n  'type': 'LANDING_PAGE',\n  'content': '<html>...</html>',\n  'productId': '22222222-3333-4444-5555-666666666666',\n  'creatorId': 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',\n  'creatorName': 'Maria Silva',\n  'creationDate': '2025-09-09T09:12:30',\n  'postDate': '2025-09-15T10:00:00',\n  'productionStartDate': '2025-09-10T09:00:00',\n  'productionEndDate': '2025-09-12T18:00:00',\n  'metaAdsId': '123456789012345',\n  'producerId': 'bbbbbbbb-cccc-dddd-eeee-ffffffffffff',\n  'producerName': 'João Almeida',\n  'status': 'IN_PRODUCTION',\n  'channels': [ { 'id': '99999999-8888-7777-6666-555555555555', 'name': 'Instagram' } ],\n  'companyId': '12121212-3434-4545-5656-676767676767',\n  'metrics': null,\n  'history': [ { 'id': '55555555-6666-7777-8888-999999999999', 'contentId': '7d8e9f10-1112-1314-1516-171819202122', 'userId': 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'userName': 'Maria Silva', 'previousStatus': null, 'newStatus': 'IN_PRODUCTION', 'changedAt': '2025-09-09T09:12:30' } ]\n}"))),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado"),
        @ApiResponse(responseCode = "400", description = "Rascunho não está aprovado ou dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ContentResponse> transformDraftToContent(
            @Parameter(description = "ID do rascunho") @PathVariable UUID draftId,
            @Parameter(description = "Dados adicionais para o conteúdo",
                examples = @ExampleObject(name = "transformDraftRequest",
                    summary = "Exemplo de request para transformar rascunho",
                    value = "{\n  'status': 'IN_PRODUCTION',\n  'channelIds': [ '99999999-8888-7777-6666-555555555555' ],\n  'postDate': '2025-09-15T10:00:00',\n  'productId': '22222222-3333-4444-5555-666666666666',\n  'producerId': 'bbbbbbbb-cccc-dddd-eeee-ffffffffffff',\n  'productionStartDate': '2025-09-10T09:00:00',\n  'productionEndDate': '2025-09-12T18:00:00',\n  'metaAdsId': '123456789012345'\n}")) @Valid @RequestBody TransformDraftRequest request) {
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
