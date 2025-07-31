package com.organixui.organixbackend.common.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de registro de nova empresa e administrador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    
    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(max = 100, message = "Nome da empresa deve ter no máximo 100 caracteres")
    private String companyName;
    
    @NotBlank(message = "Nome do administrador é obrigatório")
    @Size(max = 100, message = "Nome do administrador deve ter no máximo 100 caracteres")
    private String adminName;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String password;
}
