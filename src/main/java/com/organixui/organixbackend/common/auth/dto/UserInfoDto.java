package com.organixui.organixbackend.common.auth.dto;

import com.organixui.organixbackend.user.model.AdminType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para informações do usuário na resposta de autenticação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private UUID id;
    private String name;
    private String email;
    private AdminType adminType;
    private UUID companyId;
}
