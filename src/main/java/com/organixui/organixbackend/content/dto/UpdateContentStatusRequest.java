package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para atualização de status de conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para atualizar status de conteúdo")
public class UpdateContentStatusRequest {
    
    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Novo status do conteúdo",
        example = "POSTED",
        allowableValues = {"PENDING","IN_PRODUCTION","POSTED","PRODUCTION_FINISHED","FINISHED","CANCELED"})
    private ContentStatus status;
}
