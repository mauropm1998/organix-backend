package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para rascunho.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftResponse {
    private UUID id;
    private String title;
    private String content;
    private DraftStatus status;
    private UUID productId;
    private String productName;
    private UUID userId;
    private String userName;
    private UUID companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
