package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de rascunho.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDraftRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    @NotBlank(message = "Tipo é obrigatório") 
    private String type;
    
    @Schema(description = "ID do produto associado (opcional)")
    private java.util.UUID productId;
    
    private String content;
    
    private DraftStatus status;
}
