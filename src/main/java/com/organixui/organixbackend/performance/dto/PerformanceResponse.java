package com.organixui.organixbackend.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de resposta para métricas de performance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceResponse {
    private UUID id;
    private UUID contentId;
    private String contentTitle;
    private UUID companyId;
    private Map<String, Object> metrics;
    private LocalDateTime createdAt;
}
