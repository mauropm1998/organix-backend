package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO para atualização de métricas de conteúdo")
public class ContentMetricsRequest {

    @Min(value = 0, message = "Views must be non-negative")
    @Schema(description = "Número de visualizações", example = "1500")
    private Integer views;

    @Min(value = 0, message = "Likes must be non-negative")
    @Schema(description = "Número de curtidas", example = "150")
    private Integer likes;

    @Min(value = 0, message = "Reach must be non-negative")
    @Schema(description = "Alcance do conteúdo", example = "5000")
    private Integer reach;

    @Min(value = 0, message = "Engagement must be non-negative")
    @Schema(description = "Taxa de engajamento", example = "350")
    private Integer engagement;

    @Min(value = 0, message = "Comments must be non-negative")
    @Schema(description = "Número de comentários", example = "45")
    private Integer comments;

    @Min(value = 0, message = "Shares must be non-negative")
    @Schema(description = "Número de compartilhamentos", example = "25")
    private Integer shares;

    @Min(value = 0, message = "Impressions must be non-negative")
    @Schema(description = "Número de impressões", example = "8000")
    private Integer impressions;

    @Schema(description = "Taxa de engajamento (0.0 a 100.0)", example = "3.5")
    private Double engagementRate;

    @Schema(description = "Taxa de cliques (0.0 a 100.0)", example = "2.1")
    private Double clickThroughRate;

    @Schema(description = "Taxa de conversão (0.0 a 100.0)", example = "1.2")
    private Double conversionRate;

    @Schema(description = "Dados adicionais de métricas em formato JSON", 
            example = "{\"channel_metrics\": {\"instagram\": {\"likes\": 100, \"shares\": 10}}}")
    private Map<String, Object> metricsData;
}
