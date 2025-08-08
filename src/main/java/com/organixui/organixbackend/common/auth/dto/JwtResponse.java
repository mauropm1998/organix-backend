package com.organixui.organixbackend.common.auth.dto;

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
    private UserInfoDto user;
    private CompanyInfoDto company;
    private String token;
    private String refreshToken;
}
