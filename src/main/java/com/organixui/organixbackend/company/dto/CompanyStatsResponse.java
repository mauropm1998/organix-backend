package com.organixui.organixbackend.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para estatísticas da empresa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatsResponse {
    private UUID companyId;
    private String companyName;
    private long totalUsers;
    private long totalProducts;
    private long totalDrafts;
    private long pendingDrafts;
    private long approvedDrafts;
    private long totalContent;
    private long postedContent;
}
