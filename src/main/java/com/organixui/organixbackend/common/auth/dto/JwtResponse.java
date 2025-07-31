package com.organixui.organixbackend.common.auth.dto;

import java.util.UUID;

import com.organixui.organixbackend.user.model.AdminType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta para autenticação JWT.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID userId;
    private String email;
    private String name;
    private AdminType adminType;
    private UUID companyId;
    private String companyName;
    
    public JwtResponse(String token, UUID userId, String email, String name, AdminType adminType, UUID companyId, String companyName) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.adminType = adminType;
        this.companyId = companyId;
        this.companyName = companyName;
    }
}
