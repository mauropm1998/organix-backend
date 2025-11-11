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
 * DTO de request para conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload para criação de conteúdo")
public class ContentRequest {
    private String name;
    private String type;
    private String content;
    private UUID productId;
    
    @Schema(description = "ID do usuário produtor (opcional, se diferente do criador)")
    private UUID producerId;
    
    @Schema(description = "Status inicial do conteúdo (opcional, padrão: PENDING)")
    private ContentStatus status;
    
    @Schema(description = "Data/hora planejada de publicação (opcional)", example = "2025-09-01T10:30:00")
    private LocalDateTime postDate;
    @Schema(description = "Data/hora de início da produção (opcional)", example = "2025-08-25T09:00:00")
    private LocalDateTime productionStartDate;
    @Schema(description = "Data/hora de término da produção (opcional)", example = "2025-08-27T18:45:00")
    private LocalDateTime productionEndDate;
    @Schema(description = "Identificador único no Meta Ads (opcional)", example = "123456789012345")
    private String metaAdsId;
    
    @Schema(description = "Tipo de tráfego (PAID ou ORGANIC)")
    private TrafficType trafficType;
    
    private List<UUID> channelIds;
}
