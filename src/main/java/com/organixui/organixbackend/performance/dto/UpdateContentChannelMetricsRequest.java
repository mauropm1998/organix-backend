package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de request para atualizar métricas de todos os canais de um conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para atualizar métricas de todos os canais de um conteúdo")
public class UpdateContentChannelMetricsRequest {
    
    @Schema(description = "Lista de métricas por canal")
    @NotEmpty(message = "Lista de métricas não pode estar vazia")
    @Valid
    private List<UpdateChannelMetricRequest> channelMetrics;
}
