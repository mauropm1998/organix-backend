package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
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
    
    private String content;
    
    private DraftStatus status;
}
