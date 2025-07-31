package com.organixui.organixbackend.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para informações da empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private UUID id;
    private String name;
    private String industry;
    private String size;
    private String website;
    private String description;
    private UUID adminId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
