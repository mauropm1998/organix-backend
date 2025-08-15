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

    @Schema(description = "Curtidas no canal específico", example = "450")
    private Long channelLikes;

    @Schema(description = "Comentários no canal específico", example = "75")
    private Long channelComments;

    @Schema(description = "Compartilhamentos no canal específico", example = "30")
    private Long channelShares;

    @Schema(description = "Visitas ao site através do canal", example = "120")
    private Long channelSiteVisits;

    @Schema(description = "Novas contas criadas através do canal", example = "15")
    private Long channelNewAccounts;

    @Schema(description = "Cliques no post do canal", example = "200")
    private Long channelPostClicks;

    @Schema(description = "Data de publicação")
    private String publishDate;

    @Schema(description = "Score de performance", example = "8.5")
    private Double performanceScore;
}
