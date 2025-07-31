package com.organixui.organixbackend.calendar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa eventos do calendário de conteúdo.
 * Usado para agendar publicações e planejamento de conteúdo.
 */
@Entity
@Table(name = "calendar_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "content_id")
    private UUID contentId;
    
    @Column(name = "draft_id")
    private UUID draftId;
    
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @ElementCollection
    @CollectionTable(name = "calendar_event_channels", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "channel")
    private List<String> channels;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.SCHEDULED;
    
    @Column(name = "recurring")
    private Boolean recurring = false;
    
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;
    
    @Column(name = "all_day")
    private Boolean allDay = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
