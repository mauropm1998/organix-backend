package com.organixui.organixbackend.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de produto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    @NotBlank(message = "Nome do produto é obrigatório")
    private String name;
}
