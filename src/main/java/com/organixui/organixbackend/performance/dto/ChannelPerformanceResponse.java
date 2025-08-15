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
@Schema(description = "Resposta com performance por canal")
public class ChannelPerformanceResponse {

    @Schema(description = "Nome do canal", example = "Instagram")
    private String channelName;

    @Schema(description = "Total de visualizações do canal", example = "15000")
    private Long totalViews;

    @Schema(description = "Total de curtidas do canal", example = "800")
    private Long totalLikes;

    @Schema(description = "Total de comentários do canal", example = "120")
    private Long totalComments;

    @Schema(description = "Total de compartilhamentos do canal", example = "90")
    private Long totalShares;

    @Schema(description = "Taxa de engajamento do canal", example = "6.7")
    private Double engagementRate;

    @Schema(description = "Número de posts no canal", example = "25")
    private Long totalPosts;

    @Schema(description = "Crescimento de seguidores no canal", example = "75")
    private Long followerGrowth;

    @Schema(description = "Alcance médio por post", example = "2500")
    private Double averageReach;

    @Schema(description = "Melhor horário para postar", example = "18:00")
    private String bestPostTime;
}
