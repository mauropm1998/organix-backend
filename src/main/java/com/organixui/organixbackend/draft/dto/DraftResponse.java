package com.organixui.organixbackend.draft.dto;

import com.organixui.organixbackend.draft.model.DraftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class DraftResponse {
    private UUID id;
    private String name;
    private String type;
    private UUID productId;
    private String productName;
    private UUID creatorId;
    private String creatorName;
    private String content;
    private DraftStatus status;
    private LocalDateTime createdAt;
    private UUID companyId;
}
