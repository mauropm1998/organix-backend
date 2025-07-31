package com.organixui.organixbackend.user.dto;

import com.organixui.organixbackend.user.model.AdminType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para respostas contendo informações de usuários.
 * Não inclui dados sensíveis como senhas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private AdminType adminType;
    private UUID companyId;
    private String companyName;
}
