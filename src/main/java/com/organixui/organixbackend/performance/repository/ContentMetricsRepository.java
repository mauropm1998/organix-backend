package com.organixui.organixbackend.performance.repository;

import com.organixui.organixbackend.performance.model.ContentMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados da entidade ContentMetrics.
 */
@Repository
public interface ContentMetricsRepository extends JpaRepository<ContentMetrics, UUID> {
    
    /**
     * Busca métricas por empresa através do join com Content.
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE c.companyId = :companyId")
    List<ContentMetrics> findAllByCompanyId(@Param("companyId") UUID companyId);
    
    /**
     * Busca métricas por conteúdo.
     */
    Optional<ContentMetrics> findByContentId(UUID contentId);
    
    /**
     * Busca métricas por criador ou produtor.
     */
    @Query("SELECT cm FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "WHERE c.companyId = :companyId AND (c.creatorId = :creatorId OR c.producerId = :producerId)")
    List<ContentMetrics> findAllByCreatorIdOrProducerIdAndCompanyId(@Param("creatorId") UUID creatorId, @Param("producerId") UUID producerId, @Param("companyId") UUID companyId);
    
    /**
     * Deleta métricas por ID de conteúdo.
     */
    void deleteByContentId(UUID contentId);

    /**
     * Busca métricas agregadas por empresa com filtros opcionais.
     */
    @Query("SELECT " +
           "COALESCE(SUM(cm.views), 0) as totalViews, " +
           "COALESCE(SUM(cm.likes), 0) as totalLikes, " +
           "COALESCE(SUM(cm.comments), 0) as totalComments, " +
           "COALESCE(SUM(cm.shares), 0) as totalShares, " +
           "COALESCE(SUM(cm.reach), 0) as totalReach, " +
           "CASE WHEN COUNT(cm) > 0 THEN COALESCE(AVG(CAST(cm.engagement AS double)), 0.0) ELSE 0.0 END as avgEngagement " +
           "FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "LEFT JOIN c.channels ch " +
           "WHERE c.companyId = :companyId " +
           "AND (:startDate IS NULL OR c.postDate >= :startDate) " +
           "AND (:endDate IS NULL OR c.postDate <= :endDate) " +
           "AND (:channel IS NULL OR ch.name = :channel) " +
           "AND (:productId IS NULL OR c.productId = :productId)")
    Object[] findAggregatedMetrics(@Param("companyId") UUID companyId, 
                                  @Param("startDate") java.time.LocalDateTime startDate, 
                                  @Param("endDate") java.time.LocalDateTime endDate, 
                                  @Param("channel") String channel, 
                                  @Param("productId") UUID productId);

    /**
     * Busca métricas por canal para uma empresa.
     */
    @Query("SELECT ch.name as channelName, " +
           "COUNT(DISTINCT c.id) as totalPosts, " +
           "COALESCE(SUM(cm.views), 0) as totalViews, " +
           "COALESCE(SUM(cm.likes), 0) as totalLikes, " +
           "COALESCE(SUM(cm.comments), 0) as totalComments, " +
           "COALESCE(SUM(cm.shares), 0) as totalShares, " +
           "CASE WHEN COUNT(cm) > 0 THEN COALESCE(AVG(CAST(cm.engagement AS double)), 0.0) ELSE 0.0 END as avgEngagement, " +
           "CASE WHEN COUNT(cm) > 0 THEN COALESCE(AVG(CAST(cm.reach AS double)), 0.0) ELSE 0.0 END as avgReach " +
           "FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "JOIN c.channels ch " +
           "WHERE c.companyId = :companyId " +
           "GROUP BY ch.name " +
           "ORDER BY totalViews DESC")
    List<Object[]> findChannelPerformance(@Param("companyId") UUID companyId);

    /**
     * Busca top conteúdos por performance com filtros opcionais.
     */
    @Query("SELECT c.id as contentId, " +
           "c.name as contentName, " +
           "c.type as contentType, " +
           "c.productId as productId, " +
           "(SELECT p.name FROM Product p WHERE p.id = c.productId) as productName, " +
           "ch.name as channelName, " +
           "COALESCE(cm.views, 0) as totalViews, " +
           "COALESCE(cm.likes, 0) as totalLikes, " +
           "COALESCE(cm.comments, 0) as totalComments, " +
           "COALESCE(cm.shares, 0) as totalShares, " +
           "COALESCE(CAST(cm.engagement AS double), 0.0) as engagementRate, " +
           "c.postDate as publishDate, " +
           "(" +
           "  COALESCE(cm.views, 0) * 0.1 + " +
           "  COALESCE(cm.likes, 0) * 0.2 + " +
           "  COALESCE(cm.comments, 0) * 0.3 + " +
           "  COALESCE(cm.shares, 0) * 0.4" +
           ") as performanceScore " +
           "FROM ContentMetrics cm " +
           "JOIN Content c ON cm.contentId = c.id " +
           "LEFT JOIN c.channels ch " +
           "WHERE c.companyId = :companyId " +
           "AND (:startDate IS NULL OR c.postDate >= :startDate) " +
           "AND (:endDate IS NULL OR c.postDate <= :endDate) " +
           "AND (:channel IS NULL OR ch.name = :channel) " +
           "AND (:productId IS NULL OR c.productId = :productId) " +
           "ORDER BY performanceScore DESC")
    List<Object[]> findTopContent(@Param("companyId") UUID companyId, 
                                 @Param("startDate") java.time.LocalDateTime startDate, 
                                 @Param("endDate") java.time.LocalDateTime endDate, 
                                 @Param("channel") String channel, 
                                 @Param("productId") UUID productId);
}
