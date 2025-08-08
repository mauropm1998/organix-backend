package com.organixui.organixbackend.common.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para informações da empresa na resposta de autenticação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoDto {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private UUID adminId;
}
