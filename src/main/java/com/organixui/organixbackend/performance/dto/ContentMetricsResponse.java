package com.organixui.organixbackend.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Schema(description = "Response DTO com métricas de conteúdo")
public class ContentMetricsResponse {

    @Schema(description = "ID da métrica", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID do conteúdo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID contentId;

    @Schema(description = "ID da empresa", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID companyId;

    @Schema(description = "Número de visualizações", example = "1500")
    private Long views;

    @Schema(description = "Número de curtidas", example = "150")
    private Long likes;

    @Schema(description = "Número de compartilhamentos", example = "25")
    private Long shares;

    @Schema(description = "Número de comentários", example = "45")
    private Long comments;

    @Schema(description = "Taxa de engajamento", example = "3.5")
    private Double engagementRate;

    @Schema(description = "Alcance do conteúdo", example = "5000")
    private Long reach;

    @Schema(description = "Número de impressões", example = "8000")
    private Long impressions;

    @Schema(description = "Taxa de cliques", example = "2.1")
    private Double clickThroughRate;

    @Schema(description = "Taxa de conversão", example = "1.2")
    private Double conversionRate;

    @Schema(description = "Dados adicionais de métricas")
    private Map<String, Object> metricsData;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de última atualização", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
