package com.organixui.organixbackend.content.history.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "content_audit_log", indexes = {
        @Index(name = "idx_cal_content", columnList = "content_id"),
        @Index(name = "idx_cal_company_changed_at", columnList = "company_id,changed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentAuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "old_value", length = 1024)
    private String oldValue;

    @Column(name = "new_value", length = 1024)
    private String newValue;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onPersist() {
        if (changedAt == null) changedAt = LocalDateTime.now();
    }
}
