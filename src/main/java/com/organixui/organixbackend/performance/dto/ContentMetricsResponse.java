package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO com métricas de conteúdo")
public class ContentMetricsResponse {

    @Schema(description = "ID da métrica", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID do conteúdo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID contentId;

    @Schema(description = "Número de visualizações", example = "1500")
    private Integer views;

    @Schema(description = "Número de curtidas", example = "150")
    private Integer likes;

    @Schema(description = "Alcance do conteúdo", example = "5000")
    private Integer reach;

    @Schema(description = "Taxa de engajamento", example = "350")
    private Integer engagement;

    @Schema(description = "Número de comentários", example = "45")
    private Integer comments;

    @Schema(description = "Número de compartilhamentos", example = "25")
    private Integer shares;
    
    @Schema(description = "Métricas por canal")
    private List<ChannelMetricResponse> channelMetrics;
}
