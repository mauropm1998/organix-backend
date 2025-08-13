package com.organixui.organixbackend.content.repository;

import com.organixui.organixbackend.content.model.Content;
import com.organixui.organixbackend.content.model.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados da entidade Content.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    
    /**
     * Busca conteúdo por empresa.
     */
    List<Content> findByCompanyId(UUID companyId);
    
    /**
     * Busca conteúdo por ID e empresa.
     */
    Optional<Content> findByIdAndCompanyId(UUID id, UUID companyId);
    
    /**
     * Busca conteúdo por draft ID e empresa.
     */
    Optional<Content> findByDraftIdAndCompanyId(UUID draftId, UUID companyId);
    
    /**
     * Busca conteúdo por criador e empresa.
     */
    List<Content> findByCreatedByAndCompanyId(String createdBy, UUID companyId);
    
    /**
     * Busca conteúdo por produto e empresa.
     */
    List<Content> findByProductIdAndCompanyId(UUID productId, UUID companyId);
    
    /**
     * Busca conteúdo criado em um período específico.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId AND c.createdAt BETWEEN :startDate AND :endDate")
    List<Content> findByCompanyIdAndCreatedAtBetween(@Param("companyId") UUID companyId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Conta total de conteúdo por empresa.
     */
    long countByCompanyId(UUID companyId);
    
    /**
     * Conta conteúdo publicado por empresa.
     */
    long countByCompanyIdAndStatus(UUID companyId, ContentStatus status);
    
    /**
     * Conta conteúdo agendado por empresa.
     */
    @Query("SELECT COUNT(c) FROM Content c WHERE c.companyId = :companyId " +
           "AND c.scheduledDate IS NOT NULL AND c.scheduledDate > :now")
    long countByCompanyIdAndScheduledDateAfter(@Param("companyId") UUID companyId, 
                                              @Param("now") LocalDateTime now);
    
    /**
     * Busca conteúdo por empresa - método simples.
     */
    Page<Content> findByCompanyId(UUID companyId, Pageable pageable);
    
    /**
     * Busca conteúdo por criador e empresa - método simples.
     */
    Page<Content> findByCompanyIdAndCreatedBy(UUID companyId, String createdBy, Pageable pageable);
    
    /**
     * Busca conteúdo com filtros complexos - para Admin.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Content> findByCompanyIdWithFilters(@Param("companyId") UUID companyId,
                                            @Param("productId") UUID productId,
                                            @Param("status") ContentStatus status,
                                            Pageable pageable);
    
    /**
     * Busca conteúdo com filtros complexos - para Operator (apenas próprio conteúdo).
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId " +
           "AND c.createdBy = :createdBy " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Content> findByCompanyIdAndCreatedByWithFilters(@Param("companyId") UUID companyId,
                                                        @Param("createdBy") String createdBy,
                                                        @Param("productId") UUID productId,
                                                        @Param("status") ContentStatus status,
                                                        Pageable pageable);
    
    /**
     * Busca conteúdo agendado.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId " +
           "AND c.scheduledDate IS NOT NULL " +
           "AND c.scheduledDate > :now " +
           "AND (:productId IS NULL OR c.productId = :productId)")
    Page<Content> findByCompanyIdAndScheduledDateAfter(@Param("companyId") UUID companyId,
                                                      @Param("productId") UUID productId,
                                                      @Param("now") LocalDateTime now,
                                                      Pageable pageable);
}
