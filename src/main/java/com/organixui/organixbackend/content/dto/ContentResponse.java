package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para conteúdo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {
    private UUID id;
    private String name;
    private String type;
    private UUID productId;
    private UUID creatorId;
    private String creatorName;
    private LocalDateTime creationDate;
    private LocalDateTime postDate;
    private UUID producerId;
    private String producerName;
    private ContentStatus status;
    private List<ChannelResponse> channels;
    private UUID companyId;
    private ContentMetricsResponse metrics;
}
