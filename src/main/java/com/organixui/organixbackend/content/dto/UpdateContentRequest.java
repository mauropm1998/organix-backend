package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
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
public class UpdateContentRequest {
    private String name;
    private String type;
    private String content;
    private UUID productId;
    
    @Schema(description = "ID do usuário produtor (opcional, se diferente do criador)")
    private UUID producerId;
    
    @Schema(description = "Status do conteúdo", 
            allowableValues = {"PENDING", "CANCELED", "POSTED", "IN_PRODUCTION", "FINISHED"})
    private ContentStatus status;
    
    private LocalDateTime postDate;
    private List<UUID> channelIds;
}
