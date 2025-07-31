package com.organixui.organixbackend.performance.repository;

import com.organixui.organixbackend.performance.model.ContentMetrics;
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
 * Repositório para operações de banco de dados da entidade ContentMetrics.
 */
@Repository
public interface ContentMetricsRepository extends JpaRepository<ContentMetrics, UUID> {
    
    /**
     * Busca métricas por empresa.
     */
    List<ContentMetrics> findByCompanyId(UUID companyId);
    
    /**
     * Busca métricas por conteúdo e empresa.
     */
    Optional<ContentMetrics> findByContentIdAndCompanyId(UUID contentId, UUID companyId);
    
    /**
     * Busca métricas criadas em um período específico.
     */
    @Query("SELECT cm FROM ContentMetrics cm WHERE cm.companyId = :companyId AND cm.createdAt BETWEEN :startDate AND :endDate")
    List<ContentMetrics> findByCompanyIdAndCreatedAtBetween(@Param("companyId") UUID companyId, 
                                                           @Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Busca métricas por ID de conteúdo.
     */
    Optional<ContentMetrics> findByContentId(UUID contentId);
    
    /**
     * Deleta métricas por ID de conteúdo.
     */
    void deleteByContentId(UUID contentId);
    
    /**
     * Busca métricas com filtros complexos - para Admin.
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE cm.companyId = :companyId " +
           "AND (:contentId IS NULL OR cm.contentId = :contentId) " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:startDate IS NULL OR cm.updatedAt >= :startDate) " +
           "AND (:endDate IS NULL OR cm.updatedAt <= :endDate)")
    Page<ContentMetrics> findByCompanyIdWithFilters(@Param("companyId") UUID companyId,
                                                    @Param("contentId") UUID contentId,
                                                    @Param("productId") UUID productId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    Pageable pageable);
    
    /**
     * Busca métricas com filtros complexos - para Operator (apenas próprio conteúdo).
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE cm.companyId = :companyId " +
           "AND c.createdBy = :createdBy " +
           "AND (:contentId IS NULL OR cm.contentId = :contentId) " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:startDate IS NULL OR cm.updatedAt >= :startDate) " +
           "AND (:endDate IS NULL OR cm.updatedAt <= :endDate)")
    Page<ContentMetrics> findByCompanyIdAndCreatedByWithFilters(@Param("companyId") UUID companyId,
                                                               @Param("createdBy") String createdBy,
                                                               @Param("contentId") UUID contentId,
                                                               @Param("productId") UUID productId,
                                                               @Param("startDate") LocalDateTime startDate,
                                                               @Param("endDate") LocalDateTime endDate,
                                                               Pageable pageable);
    
    /**
     * Busca métricas para relatórios - Admin.
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE cm.companyId = :companyId " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:startDate IS NULL OR cm.updatedAt >= :startDate) " +
           "AND (:endDate IS NULL OR cm.updatedAt <= :endDate)")
    List<ContentMetrics> findMetricsForReport(@Param("companyId") UUID companyId,
                                              @Param("productId") UUID productId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * Busca métricas para relatórios - Operator.
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE cm.companyId = :companyId " +
           "AND c.createdBy = :createdBy " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "AND (:startDate IS NULL OR cm.updatedAt >= :startDate) " +
           "AND (:endDate IS NULL OR cm.updatedAt <= :endDate)")
    List<ContentMetrics> findMetricsForReportByUser(@Param("companyId") UUID companyId,
                                                    @Param("createdBy") String createdBy,
                                                    @Param("productId") UUID productId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}
