package com.organixui.organixbackend.content.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "content", indexes = {
    @Index(name = "idx_content_company_creation_date", columnList = "company_id,creation_date"),
    @Index(name = "idx_content_company_post_date", columnList = "company_id,post_date"),
    @Index(name = "idx_content_company_status", columnList = "company_id,status"),
    @Index(name = "idx_content_company_product", columnList = "company_id,product_id"),
    @Index(name = "idx_content_company_traffic_type", columnList = "company_id,traffic_type"),
    @Index(name = "idx_content_company_creator", columnList = "company_id,creator_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;
    
    @Column(name = "producer_id")
    private UUID producerId;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "traffic_type")
    private TrafficType trafficType;
    
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
    
    @Column(name = "post_date")
    private LocalDateTime postDate;

    // Início da produção do conteúdo
    @Column(name = "production_start_date")
    private LocalDateTime productionStartDate;

    // Término da produção do conteúdo
    @Column(name = "production_end_date")
    private LocalDateTime productionEndDate;

    // Identificador único no Meta Ads (opcional)
    @Column(name = "meta_ads_id")
    private String metaAdsId;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "content_channels",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private List<Channel> channels;

    // Texto/conteúdo do post
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }
}
