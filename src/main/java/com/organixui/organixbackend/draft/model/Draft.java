package com.organixui.organixbackend.draft.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um rascunho de conteúdo.
 * Rascunhos são criados pelos usuários e podem ser aprovados para se tornarem conteúdo.
 */
@Entity
@Table(name = "drafts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Draft {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftStatus status = DraftStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
}
