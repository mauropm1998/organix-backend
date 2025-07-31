package com.organixui.organixbackend.content.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para transformar rascunho em conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformDraftRequest {
    
    @NotNull(message = "ID do rascunho é obrigatório")
    private UUID draftId;
    
    @NotEmpty(message = "Pelo menos um canal deve ser especificado")
    private List<String> channels;
}
