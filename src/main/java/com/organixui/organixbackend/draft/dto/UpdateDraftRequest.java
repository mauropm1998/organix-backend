package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String title;
    
    @Size(max = 5000, message = "Conteúdo deve ter no máximo 5000 caracteres")
    private String content;
    
    private DraftStatus status;
}
