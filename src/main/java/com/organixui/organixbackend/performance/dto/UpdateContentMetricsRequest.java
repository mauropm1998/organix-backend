package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de request para atualizar métricas de conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para atualizar métricas de conteúdo")
public class UpdateContentMetricsRequest {
    
    @Schema(description = "Número de visualizações")
    @Min(value = 0, message = "Visualizações não podem ser negativas")
    private Integer views;
    
    @Schema(description = "Número de curtidas")
    @Min(value = 0, message = "Curtidas não podem ser negativas")
    private Integer likes;
    
    @Schema(description = "Alcance do conteúdo")
    @Min(value = 0, message = "Alcance não pode ser negativo")
    private Integer reach;
    
    @Schema(description = "Engajamento total")
    @Min(value = 0, message = "Engajamento não pode ser negativo")
    private Integer engagement;
    
    @Schema(description = "Número de comentários")
    @Min(value = 0, message = "Comentários não podem ser negativos")
    private Integer comments;
    
    @Schema(description = "Número de compartilhamentos")
    @Min(value = 0, message = "Compartilhamentos não podem ser negativos")
    private Integer shares;
    
    @Schema(description = "Métricas por canal")
    private List<UpdateChannelMetricRequest> channelMetrics;
}
