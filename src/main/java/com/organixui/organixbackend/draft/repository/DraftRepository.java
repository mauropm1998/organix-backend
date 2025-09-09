package com.organixui.organixbackend.draft.repository;

import com.organixui.organixbackend.draft.model.Draft;
import com.organixui.organixbackend.draft.model.DraftStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Draft> findByCreatorIdAndCompanyId(UUID creatorId, UUID companyId);
    
    /**
     * Busca rascunhos por criador.
     */
    List<Draft> findByCreatorId(UUID creatorId);
    
    /**
     * Busca rascunhos por status e empresa.
     */
    List<Draft> findByStatusAndCompanyId(DraftStatus status, UUID companyId);
    
    /**
     * Busca rascunhos por status e criador.
     */
    List<Draft> findByStatusAndCreatorId(DraftStatus status, UUID creatorId);
    
    /**
     * Busca rascunhos por status, criador e empresa.
     */
    List<Draft> findByStatusAndCreatorIdAndCompanyId(DraftStatus status, UUID creatorId, UUID companyId);
    
    /**
     * Conta total de rascunhos por empresa.
     */
    long countByCompanyId(UUID companyId);
    
    /**
     * Conta rascunhos por status e empresa.
     */
    long countByCompanyIdAndStatus(UUID companyId, DraftStatus status);

    List<Draft> findByCompanyIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(UUID companyId, java.time.LocalDateTime date);

    @Query("SELECT d FROM Draft d WHERE d.companyId = :companyId " +
        "AND (:status IS NULL OR d.status = :status) " +
            "AND (:creatorId IS NULL OR d.creatorId = :creatorId) " +
            "AND d.createdAt >= :fromDate ORDER BY d.createdAt DESC")
    Page<Draft> searchDrafts(@Param("companyId") UUID companyId,
                 @Param("status") DraftStatus status,
                 @Param("creatorId") UUID creatorId,
                             @Param("fromDate") java.time.LocalDateTime fromDate,
                 Pageable pageable);
}
