package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.model.TrafficType;
import io.swagger.v3.oas.annotations.media.Schema;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para conteúdo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de conteúdo detalhada")
public class ContentResponse {
    private UUID id;
    private String name;
    private String type;
    private String content;
    private UUID productId;
    private String productName;
    private UUID creatorId;
    private String creatorName;
    private LocalDateTime creationDate;
    @Schema(description = "Data/hora de publicação efetiva ou planejada", example = "2025-09-01T10:30:00")
    private LocalDateTime postDate;
    @Schema(description = "Data/hora de início da produção", example = "2025-08-25T09:00:00")
    private LocalDateTime productionStartDate;
    @Schema(description = "Data/hora de término da produção", example = "2025-08-27T18:45:00")
    private LocalDateTime productionEndDate;
    @Schema(description = "Identificador no Meta Ads", example = "123456789012345")
    private String metaAdsId;
    private UUID producerId;
    private String producerName;
    @Schema(description = "Status do conteúdo",
        allowableValues = {"PENDING","IN_PRODUCTION","POSTED","PRODUCTION_FINISHED","FINISHED","CANCELED"})
    private ContentStatus status;
    @Schema(description = "Tipo de tráfego (PAID ou ORGANIC)")
    private TrafficType trafficType;
    private List<ChannelResponse> channels;
    private UUID companyId;
    private ContentMetricsResponse metrics;
    @Schema(description = "Histórico de mudanças de status (ordenado asc por data)")
    private List<com.organixui.organixbackend.content.history.dto.ContentStatusHistoryResponse> history;
}
