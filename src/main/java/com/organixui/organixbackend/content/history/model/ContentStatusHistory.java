package com.organixui.organixbackend.content.history.model;

import com.organixui.organixbackend.content.model.Content;
import com.organixui.organixbackend.content.model.ContentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "content_status_history", indexes = {
        @Index(name = "idx_csh_content", columnList = "content_id"),
        @Index(name = "idx_csh_content_changed_at", columnList = "content_id,changed_at"),
        @Index(name = "idx_csh_company_changed_at", columnList = "company_id,changed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentStatusHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ContentStatus newStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ContentStatus previousStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @PrePersist
    protected void onPersist() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}
