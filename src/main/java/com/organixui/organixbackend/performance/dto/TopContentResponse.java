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
@Schema(description = "Resposta com top conteúdos por performance")
public class TopContentResponse {

    @Schema(description = "ID do conteúdo")
    private String contentId;

    @Schema(description = "Nome do conteúdo", example = "Post sobre produto X")
    private String contentName;

    @Schema(description = "Tipo do conteúdo", example = "Post")
    private String contentType;

    @Schema(description = "ID do produto", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productId;

    @Schema(description = "Nome do produto", example = "Produto X")
    private String productName;

    @Schema(description = "Canal onde foi publicado", example = "Instagram")
    private String channel;

    @Schema(description = "Total de visualizações", example = "5000")
    private Long totalViews;

    @Schema(description = "Total de curtidas", example = "450")
    private Long totalLikes;

    @Schema(description = "Total de comentários", example = "75")
    private Long totalComments;

    @Schema(description = "Total de compartilhamentos", example = "30")
    private Long totalShares;

    @Schema(description = "Taxa de engajamento", example = "9.2")
    private Double engagementRate;

    @Schema(description = "Data de publicação")
    private String publishDate;

    @Schema(description = "Score de performance", example = "8.5")
    private Double performanceScore;
}
