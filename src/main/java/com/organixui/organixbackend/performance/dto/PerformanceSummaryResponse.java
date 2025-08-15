package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com resumo de performance geral")
public class PerformanceSummaryResponse {

    @Schema(description = "Total de visualizações", example = "25000")
    private Long totalViews;

    @Schema(description = "Total de curtidas", example = "1500")
    private Long totalLikes;

    @Schema(description = "Total de comentários", example = "300")
    private Long totalComments;

    @Schema(description = "Total de compartilhamentos", example = "200")
    private Long totalShares;

    @Schema(description = "Taxa de engajamento média", example = "8.5")
    private Double averageEngagementRate;

    @Schema(description = "Crescimento de seguidores no período", example = "150")
    private Long followerGrowth;

    @Schema(description = "Conteúdo mais popular")
    private String topContent;

    @Schema(description = "Canal com melhor performance")
    private String topChannel;

    @Schema(description = "Número total de posts no período", example = "45")
    private Long totalPosts;

    @Schema(description = "Alcance total", example = "50000")
    private Long totalReach;
}
