package com.organixui.organixbackend.draft.model;

/**
 * Enum que representa os possíveis status de um rascunho.
 */
public enum DraftStatus {
    DRAFT,      // Rascunho em edição
    REVIEW,     // Em revisão
    APPROVED,   // Aprovado para conversão em conteúdo
    REJECTED    // Rejeitado
}
