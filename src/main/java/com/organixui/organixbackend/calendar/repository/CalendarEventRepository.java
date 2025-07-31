package com.organixui.organixbackend.calendar.repository;

import com.organixui.organixbackend.calendar.model.CalendarEvent;
import com.organixui.organixbackend.calendar.model.EventStatus;
import com.organixui.organixbackend.calendar.model.EventType;
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
 * Repositório para operações de banco de dados da entidade CalendarEvent.
 */
@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {
    
    /**
     * Busca eventos por empresa.
     */
    List<CalendarEvent> findByCompanyId(UUID companyId);
    
    /**
     * Busca evento por ID e empresa.
     */
    Optional<CalendarEvent> findByIdAndCompanyId(UUID id, UUID companyId);
    
    /**
     * Busca eventos por criador e empresa.
     */
    Page<CalendarEvent> findByCompanyIdAndCreatedBy(UUID companyId, String createdBy, Pageable pageable);
    
    /**
     * Busca eventos em um período específico.
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.companyId = :companyId " +
           "AND e.startDate >= :startDate AND e.startDate <= :endDate")
    List<CalendarEvent> findByCompanyIdAndDateRange(@Param("companyId") UUID companyId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Busca eventos com filtros complexos.
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.companyId = :companyId " +
           "AND (:eventType IS NULL OR e.eventType = :eventType) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:productId IS NULL OR e.productId = :productId) " +
           "AND (:startDate IS NULL OR e.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.startDate <= :endDate)")
    Page<CalendarEvent> findByCompanyIdWithFilters(@Param("companyId") UUID companyId,
                                                   @Param("eventType") EventType eventType,
                                                   @Param("status") EventStatus status,
                                                   @Param("productId") UUID productId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);
    
    /**
     * Busca eventos por conteúdo.
     */
    List<CalendarEvent> findByContentIdAndCompanyId(UUID contentId, UUID companyId);
    
    /**
     * Busca eventos por draft.
     */
    List<CalendarEvent> findByDraftIdAndCompanyId(UUID draftId, UUID companyId);
    
    /**
     * Busca eventos recorrentes.
     */
    List<CalendarEvent> findByCompanyIdAndRecurringTrue(UUID companyId);
    
    /**
     * Busca eventos agendados para hoje.
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.companyId = :companyId " +
           "AND e.status = 'SCHEDULED' " +
           "AND DATE(e.startDate) = DATE(:today)")
    List<CalendarEvent> findTodaysEvents(@Param("companyId") UUID companyId,
                                        @Param("today") LocalDateTime today);
    
    /**
     * Busca próximos eventos (próximos 7 dias).
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.companyId = :companyId " +
           "AND e.status = 'SCHEDULED' " +
           "AND e.startDate BETWEEN :start AND :end " +
           "ORDER BY e.startDate ASC")
    List<CalendarEvent> findUpcomingEvents(@Param("companyId") UUID companyId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}
