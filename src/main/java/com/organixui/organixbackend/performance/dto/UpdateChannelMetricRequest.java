package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de request para atualizar métricas de canal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para atualizar métricas de um canal específico")
public class UpdateChannelMetricRequest {
    
    @Schema(description = "ID do canal")
    @NotNull(message = "ID do canal é obrigatório")
    private UUID channelId;
    
    @Schema(description = "Número de curtidas")
    @Min(value = 0, message = "Curtidas não podem ser negativas")
    private Integer likes;
    
    @Schema(description = "Número de comentários")
    @Min(value = 0, message = "Comentários não podem ser negativos")
    private Integer comments;
    
    @Schema(description = "Número de compartilhamentos")
    @Min(value = 0, message = "Compartilhamentos não podem ser negativos")
    private Integer shares;
    
    @Schema(description = "Visitas ao site")
    @Min(value = 0, message = "Visitas ao site não podem ser negativas")
    private Integer siteVisits;
    
    @Schema(description = "Novas contas criadas")
    @Min(value = 0, message = "Novas contas não podem ser negativas")
    private Integer newAccounts;
    
    @Schema(description = "Cliques no post")
    @Min(value = 0, message = "Cliques no post não podem ser negativos")
    private Integer postClicks;
}
