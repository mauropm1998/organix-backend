package com.organixui.organixbackend.performance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa dados de métricas por canal.
 */
@Entity
@Table(name = "channel_metric_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMetricData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "channel_id")
    private UUID channelId;
    
    @Column(name = "channel_name")
    private String channelName;
    
    @Column(name = "likes")
    private Integer likes = 0;
    
    @Column(name = "comments")
    private Integer comments = 0;
    
    @Column(name = "shares")
    private Integer shares = 0;
    
    @Column(name = "site_visits")
    private Integer siteVisits = 0;
    
    @Column(name = "new_accounts")
    private Integer newAccounts = 0;
    
    @Column(name = "post_clicks")
    private Integer postClicks = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_metrics_id")
    private ContentMetrics contentMetrics;
}
