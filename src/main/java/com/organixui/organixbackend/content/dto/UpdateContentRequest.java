package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.model.TrafficType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de request para atualizar conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload para atualização parcial de conteúdo")
public class UpdateContentRequest {
    private String name;
    private String type;
    private String content;
    private UUID productId;
    
    @Schema(description = "ID do usuário produtor (opcional, se diferente do criador)")
    private UUID producerId;
    
    @Schema(description = "Status do conteúdo", 
        allowableValues = {"PENDING", "CANCELED", "POSTED", "IN_PRODUCTION", "PRODUCTION_FINISHED", "FINISHED"})
    private ContentStatus status;
    
    @Schema(description = "Nova data/hora de publicação (opcional)", example = "2025-09-02T14:00:00")
    private LocalDateTime postDate;
    @Schema(description = "Nova data/hora de início de produção (opcional)", example = "2025-08-26T08:15:00")
    private LocalDateTime productionStartDate;
    @Schema(description = "Nova data/hora de término de produção (opcional)", example = "2025-08-28T17:30:00")
    private LocalDateTime productionEndDate;
    @Schema(description = "Identificador no Meta Ads (opcional)", example = "123456789012345")
    private String metaAdsId;
    @Schema(description = "Tipo de tráfego (PAID ou ORGANIC)")
    private TrafficType trafficType;
    private List<UUID> channelIds;
}
