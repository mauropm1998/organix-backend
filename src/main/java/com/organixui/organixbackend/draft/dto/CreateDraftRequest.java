package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para criação de rascunho.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDraftRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    @NotBlank(message = "Tipo é obrigatório")
    private String type;
    
    @NotNull(message = "ID do criador é obrigatório")
    private UUID creatorId;
    
    private String content;
    
    @Schema(description = "Status do rascunho. Se não informado, será criado como PENDING", 
            example = "PENDING", allowableValues = {"PENDING", "APPROVED", "NOT_APPROVED"})
    private DraftStatus status;
}
