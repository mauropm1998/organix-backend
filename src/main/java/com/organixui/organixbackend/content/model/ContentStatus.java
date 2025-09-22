package com.organixui.organixbackend.content.model;

/**
 * Enum que representa os possíveis status de conteúdo.
 */
public enum ContentStatus {
    PENDING,        // Pendente
    CANCELED,       // Cancelado
    POSTED,         // Postado
    IN_PRODUCTION,  // Em produção
    PRODUCTION_FINISHED, // Produção finalizada
    FINISHED        // Finalizado (pós-publicação)
}
