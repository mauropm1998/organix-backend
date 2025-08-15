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
     * Busca conteúdo por empresa ordenado por data de criação (desc).
     */
    List<Content> findByCompanyIdOrderByCreationDateDesc(UUID companyId);
    
    /**
     * Busca conteúdo por criador ou produtor ordenado por data de criação (desc).
     */
    List<Content> findByCreatorIdOrProducerIdOrderByCreationDateDesc(UUID creatorId, UUID producerId);
    
    /**
     * Busca conteúdo por ID e empresa.
     */
    Optional<Content> findByIdAndCompanyId(UUID id, UUID companyId);
    
    /**
     * Busca conteúdo por criador e empresa.
     */
    List<Content> findByCreatorIdAndCompanyId(UUID creatorId, UUID companyId);
    
    /**
     * Busca conteúdo por criador ou produtor e empresa.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId AND (c.creatorId = :creatorId OR c.producerId = :producerId)")
    List<Content> findByCreatorIdOrProducerIdAndCompanyId(@Param("creatorId") UUID creatorId, @Param("producerId") UUID producerId, @Param("companyId") UUID companyId);
    
    /**
     * Busca conteúdo por status e empresa.
     */
    List<Content> findByStatusAndCompanyId(ContentStatus status, UUID companyId);
    
    /**
     * Busca conteúdo por status, criador ou produtor e empresa.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId AND c.status = :status AND (c.creatorId = :creatorId OR c.producerId = :producerId)")
    List<Content> findByStatusAndCreatorIdOrProducerIdAndCompanyId(@Param("status") ContentStatus status, @Param("creatorId") UUID creatorId, @Param("producerId") UUID producerId, @Param("companyId") UUID companyId);
    
    /**
     * Busca conteúdo por produto e empresa.
     */
    List<Content> findByProductIdAndCompanyId(UUID productId, UUID companyId);
    
    /**
     * Busca conteúdo criado em um período específico.
     */
    @Query("SELECT c FROM Content c WHERE c.companyId = :companyId AND c.creationDate BETWEEN :startDate AND :endDate")
    List<Content> findByCompanyIdAndCreationDateBetween(@Param("companyId") UUID companyId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Conta total de conteúdo por empresa.
     */
    long countByCompanyId(UUID companyId);
    
    /**
     * Conta conteúdo por status e empresa.
    /**
     * Conta conteúdo por empresa e status.
     */
    long countByCompanyIdAndStatus(UUID companyId, ContentStatus status);
    
    /**
     * Busca conteúdo por empresa - método simples.
     */
    Page<Content> findByCompanyId(UUID companyId, Pageable pageable);
    
    /**
     * Busca conteúdo por criador ou produtor com paginação.
     */
    Page<Content> findByCreatorIdOrProducerId(UUID creatorId, UUID producerId, Pageable pageable);
}
