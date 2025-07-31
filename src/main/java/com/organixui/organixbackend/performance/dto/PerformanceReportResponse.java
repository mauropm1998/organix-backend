package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Response DTO com relatório de performance")
public class PerformanceReportResponse {

    @Schema(description = "Resumo geral das métricas")
    private PerformanceSummary summary;

    @Schema(description = "Métricas por período")
    private Map<String, Object> periodMetrics;

    @Schema(description = "Comparação com período anterior")
    private Map<String, Object> comparison;

    @Schema(description = "Melhores conteúdos por métrica")
    private Map<String, Object> topContent;

    @Data
    @Schema(description = "Resumo das métricas de performance")
    public static class PerformanceSummary {
        
        @Schema(description = "Total de visualizações", example = "15000")
        private Long totalViews;

        @Schema(description = "Total de curtidas", example = "1500")
        private Long totalLikes;

        @Schema(description = "Total de compartilhamentos", example = "250")
        private Long totalShares;

        @Schema(description = "Total de comentários", example = "450")
        private Long totalComments;

        @Schema(description = "Alcance total", example = "50000")
        private Long totalReach;

        @Schema(description = "Total de impressões", example = "80000")
        private Long totalImpressions;

        @Schema(description = "Taxa média de engajamento", example = "3.5")
        private Double averageEngagementRate;

        @Schema(description = "Taxa média de cliques", example = "2.1")
        private Double averageClickThroughRate;

        @Schema(description = "Taxa média de conversão", example = "1.2")
        private Double averageConversionRate;

        @Schema(description = "Número total de conteúdos", example = "25")
        private Long totalContent;
    }
}
