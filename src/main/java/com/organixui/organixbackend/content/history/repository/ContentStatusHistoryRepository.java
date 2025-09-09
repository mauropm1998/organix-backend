package com.organixui.organixbackend.content.history.repository;

import com.organixui.organixbackend.content.history.model.ContentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContentStatusHistoryRepository extends JpaRepository<ContentStatusHistory, UUID> {
    List<ContentStatusHistory> findByContentIdOrderByChangedAtAsc(UUID contentId);
}
