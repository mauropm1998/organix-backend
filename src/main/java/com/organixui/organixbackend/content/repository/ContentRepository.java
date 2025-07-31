package com.organixui.organixbackend.content.repository;

import com.organixui.organixbackend.content.model.Content;
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
     * Busca conteúdo com filtros complexos - para Admin.
     */
    @Query(value = "SELECT * FROM content c WHERE c.company_id = :companyId " +
           "AND (:productId IS NULL OR c.product_id = :productId) " +
           "AND (:published IS NULL OR c.published = :published) " +
           "AND (:channel IS NULL OR JSON_CONTAINS(c.channels, JSON_QUOTE(:channel)))",
           nativeQuery = true)
    Page<Content> findByCompanyIdWithFilters(@Param("companyId") String companyId,
                                            @Param("productId") String productId,
                                            @Param("published") Boolean published,
                                            @Param("channel") String channel,
                                            Pageable pageable);
    
    /**
     * Busca conteúdo com filtros complexos - para Operator (apenas próprio conteúdo).
     */
    @Query(value = "SELECT * FROM content c WHERE c.company_id = :companyId " +
           "AND c.created_by = :createdBy " +
           "AND (:productId IS NULL OR c.product_id = :productId) " +
           "AND (:published IS NULL OR c.published = :published) " +
           "AND (:channel IS NULL OR JSON_CONTAINS(c.channels, JSON_QUOTE(:channel)))",
           nativeQuery = true)
    Page<Content> findByCompanyIdAndCreatedByWithFilters(@Param("companyId") String companyId,
                                                        @Param("createdBy") String createdBy,
                                                        @Param("productId") String productId,
                                                        @Param("published") Boolean published,
                                                        @Param("channel") String channel,
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
