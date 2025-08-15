package com.organixui.organixbackend.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de dados da empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    
    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(max = 100, message = "Nome da empresa deve ter no máximo 100 caracteres")
    private String name;
}
