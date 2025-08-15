package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de resposta para métricas de canal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Métricas de performance por canal")
public class ChannelMetricResponse {
    
    @Schema(description = "ID da métrica do canal")
    private UUID id;
    
    @Schema(description = "Nome do canal")
    private String channelName;
    
    @Schema(description = "ID do canal")
    private UUID channelId;
    
    @Schema(description = "Número de curtidas")
    private Integer likes;
    
    @Schema(description = "Número de comentários")
    private Integer comments;
    
    @Schema(description = "Número de compartilhamentos")
    private Integer shares;
    
    @Schema(description = "Visitas ao site")
    private Integer siteVisits;
    
    @Schema(description = "Novas contas criadas")
    private Integer newAccounts;
    
    @Schema(description = "Cliques no post")
    private Integer postClicks;
}
