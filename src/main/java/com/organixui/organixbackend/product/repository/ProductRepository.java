package com.organixui.organixbackend.product.repository;

import com.organixui.organixbackend.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados relacionadas aos produtos.
 * Inclui consultas específicas para multi-tenancy.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Lista todos os produtos de uma empresa específica.
     */
    List<Product> findByCompanyId(UUID companyId);
    
    /**
     * Busca produto por ID dentro de uma empresa específica.
     */
    Optional<Product> findByIdAndCompanyId(UUID id, UUID companyId);
}
