package com.organixui.organixbackend.draft.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 500)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftStatus status = DraftStatus.DRAFT;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
