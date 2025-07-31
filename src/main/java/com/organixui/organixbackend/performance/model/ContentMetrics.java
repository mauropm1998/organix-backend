package com.organixui.organixbackend.performance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entidade que representa métricas de performance de conteúdo.
 * Armazena dados de engajamento e performance por canal.
 */
@Entity
@Table(name = "content_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "content_id", nullable = false)
    private UUID contentId;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Column(name = "views")
    private Long views = 0L;
    
    @Column(name = "likes")
    private Long likes = 0L;
    
    @Column(name = "shares")
    private Long shares = 0L;
    
    @Column(name = "comments")
    private Long comments = 0L;
    
    @Column(name = "reach")
    private Long reach = 0L;
    
    @Column(name = "impressions")
    private Long impressions = 0L;
    
    @Column(name = "engagement_rate")
    private Double engagementRate = 0.0;
    
    @Column(name = "click_through_rate")
    private Double clickThroughRate = 0.0;
    
    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics_data", columnDefinition = "JSON")
    private Map<String, Object> metricsData;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
