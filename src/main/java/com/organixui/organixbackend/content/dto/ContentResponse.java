package com.organixui.organixbackend.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para conteúdo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {
    private UUID id;
    private String title;
    private String description;
    private String content;
    private UUID productId;
    private UUID companyId;
    private String createdBy;
    private UUID draftId;
    private List<String> channels;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
