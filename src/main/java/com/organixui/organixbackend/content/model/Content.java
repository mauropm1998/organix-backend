package com.organixui.organixbackend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa conteúdo publicado.
 * Conteúdo é criado a partir de rascunhos aprovados.
 */
@Entity
@Table(name = "content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 500)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @Column(name = "draft_id")
    private UUID draftId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "channels", columnDefinition = "JSON")
    private List<String> channels;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
