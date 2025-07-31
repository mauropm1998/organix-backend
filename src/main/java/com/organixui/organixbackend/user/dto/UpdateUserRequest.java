package com.organixui.organixbackend.user.dto;

import com.organixui.organixbackend.user.model.AdminType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para requisições de atualização de usuários existentes.
 * A senha é opcional - se não fornecida, mantém a atual.
 */
@Data
public class UpdateUserRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password; // Campo opcional para atualização
    
    @NotNull(message = "Admin type is required")
    private AdminType adminType;
}
