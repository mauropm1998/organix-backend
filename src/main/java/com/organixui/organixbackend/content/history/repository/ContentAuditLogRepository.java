package com.organixui.organixbackend.content.history.repository;

import com.organixui.organixbackend.content.history.model.ContentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContentAuditLogRepository extends JpaRepository<ContentAuditLog, UUID> {
    List<ContentAuditLog> findByContentIdOrderByChangedAtAsc(UUID contentId);
    
    void deleteByContentId(UUID contentId);
}
