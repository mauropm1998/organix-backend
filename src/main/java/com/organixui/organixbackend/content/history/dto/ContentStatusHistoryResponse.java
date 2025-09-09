package com.organixui.organixbackend.content.history.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item do histórico de status do conteúdo")
public class ContentStatusHistoryResponse {
    @Schema(example = "6f1b9c2a-8d6a-4f3e-9c1d-2a4b6c8d9e10")
    private UUID id;
    @Schema(example = "3b8d2a1c-7e4f-4f2a-9c6d-1e2b3a4c5d6e")
    private UUID contentId;
    @Schema(example = "2a9c6d1e-3b8d-4f2a-7e4f-1c5d6e3b4a2b")
    private UUID userId;
    @Schema(example = "João Almeida")
    private String userName;
    @Schema(example = "IN_PRODUCTION")
    private ContentStatus newStatus;
    @Schema(example = "PENDING")
    private ContentStatus previousStatus;
    @Schema(example = "2025-08-26T09:15:00")
    private LocalDateTime changedAt;
}
