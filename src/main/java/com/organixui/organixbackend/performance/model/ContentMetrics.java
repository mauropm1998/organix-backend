package com.organixui.organixbackend.performance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa métricas de performance de conteúdo.
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
    
    @Column(name = "views")
    private Integer views = 0;
    
    @Column(name = "likes")
    private Integer likes = 0;
    
    @Column(name = "reach")
    private Integer reach = 0;
    
    @Column(name = "engagement")
    private Integer engagement = 0;
    
    @Column(name = "comments")
    private Integer comments = 0;
    
    @Column(name = "shares")
    private Integer shares = 0;
    
    @OneToMany(mappedBy = "contentMetrics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelMetricData> channelMetrics;
}
