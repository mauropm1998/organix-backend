package com.organixui.organixbackend.draft.repository;

import com.organixui.organixbackend.draft.model.Draft;
import com.organixui.organixbackend.draft.model.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados da entidade Draft.
 */
@Repository
public interface DraftRepository extends JpaRepository<Draft, UUID> {
    
    /**
     * Busca rascunhos por empresa.
     */
    List<Draft> findByCompanyId(UUID companyId);
    
    /**
     * Busca rascunho por ID e empresa.
     */
    Optional<Draft> findByIdAndCompanyId(UUID id, UUID companyId);
    
    /**
     * Busca rascunhos por criador e empresa.
     */
    List<Draft> findByCreatedByAndCompanyId(String createdBy, UUID companyId);
    
    /**
     * Busca rascunhos por status e empresa.
     */
    List<Draft> findByStatusAndCompanyId(DraftStatus status, UUID companyId);
    
    /**
     * Busca rascunhos por produto e empresa.
     */
    List<Draft> findByProductIdAndCompanyId(UUID productId, UUID companyId);
    
    /**
     * Busca rascunhos por status, produto e empresa.
     */
    List<Draft> findByStatusAndProductIdAndCompanyId(DraftStatus status, UUID productId, UUID companyId);
    
    /**
     * Busca rascunhos por criador, status e empresa.
     */
    List<Draft> findByStatusAndCreatedByAndCompanyId(DraftStatus status, String createdBy, UUID companyId);
    
    /**
     * Busca rascunhos por criador, produto e empresa.
     */
    List<Draft> findByProductIdAndCreatedByAndCompanyId(UUID productId, String createdBy, UUID companyId);
    
    /**
     * Busca rascunhos por status, produto, criador e empresa.
     */
    List<Draft> findByStatusAndProductIdAndCreatedByAndCompanyId(DraftStatus status, UUID productId, String createdBy, UUID companyId);
    
    /**
     * Conta total de rascunhos por empresa.
     */
    long countByCompanyId(UUID companyId);
    
    /**
     * Conta rascunhos por status e empresa.
     */
    long countByCompanyIdAndStatus(UUID companyId, DraftStatus status);
}
