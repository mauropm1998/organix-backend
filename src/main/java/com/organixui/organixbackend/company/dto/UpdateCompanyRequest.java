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
    
    @Size(max = 50, message = "Setor deve ter no máximo 50 caracteres")
    private String industry;
    
    @Size(max = 20, message = "Tamanho da empresa deve ter no máximo 20 caracteres")
    private String size;
    
    @Size(max = 255, message = "Website deve ter no máximo 255 caracteres")
    private String website;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
}
