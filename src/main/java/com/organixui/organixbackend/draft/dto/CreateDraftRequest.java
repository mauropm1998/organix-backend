package com.organixui.organixbackend.draft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String title;
    
    @Size(max = 5000, message = "Conteúdo deve ter no máximo 5000 caracteres")
    private String content;
    
    @NotNull(message = "ID do produto é obrigatório")
    private UUID productId;
}
