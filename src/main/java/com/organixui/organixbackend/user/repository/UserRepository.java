package com.organixui.organixbackend.user.repository;

import com.organixui.organixbackend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados relacionadas aos usuários.
 * Inclui consultas específicas para multi-tenancy (filtro por empresa).
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Busca usuário por email (usado na autenticação).
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se existe usuário com determinado email.
     */
    boolean existsByEmail(String email);
    
    /**
     * Lista todos os usuários de uma empresa específica.
     */
    List<User> findByCompanyId(UUID companyId);
    
    /**
     * Lista todos os usuários de uma empresa específica com paginação.
     */
    Page<User> findByCompanyId(UUID companyId, Pageable pageable);
    
    /**
     * Busca usuário por ID dentro de uma empresa específica.
     * Garante isolamento de dados entre empresas.
     */
    Optional<User> findByIdAndCompanyId(UUID id, UUID companyId);
    
    /**
     * Conta o número de usuários em uma empresa.
     */
    long countByCompanyId(UUID companyId);
}
