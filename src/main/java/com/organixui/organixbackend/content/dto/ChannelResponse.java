package com.organixui.organixbackend.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para canal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de canal")
public class ChannelResponse {
    
    @Schema(description = "ID único do canal", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Nome do canal", example = "Facebook")
    private String name;
    
    @Schema(description = "Data de criação do canal")
    private LocalDateTime createdAt;
}
