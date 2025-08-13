package com.organixui.organixbackend.content.controller;

import com.organixui.organixbackend.content.dto.ContentRequest;
import com.organixui.organixbackend.content.dto.ContentResponse;
import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Content management endpoints")
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/from-draft/{draftId}")
    @Operation(summary = "Create content from approved draft")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> createContentFromDraft(
            @Parameter(description = "ID of the approved draft") @PathVariable UUID draftId) {
        ContentResponse content = contentService.createContentFromDraft(draftId);
        return ResponseEntity.status(HttpStatus.CREATED).body(content);
    }

    @PostMapping
    @Operation(summary = "Create content directly (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        ContentResponse content = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(content);
    }

    @GetMapping
    @Operation(summary = "Get all content with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Page<ContentResponse>> getAllContent(
            @RequestParam(required = false) @Parameter(description = "Filter by content status") ContentStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by channel") String channel,
            Pageable pageable) {
        Page<ContentResponse> content = contentService.getAllContent(status, channel, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get content by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable UUID id) {
        ContentResponse content = contentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable UUID id,
            @Valid @RequestBody ContentRequest request) {
        ContentResponse content = contentService.updateContent(id, request);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Void> deleteContent(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish content to channels")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> publishContent(
            @PathVariable UUID id,
            @RequestParam @Parameter(description = "Channels to publish to") String[] channels) {
        ContentResponse content = contentService.publishContent(id, channels);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish content from channels")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> unpublishContent(
            @PathVariable UUID id,
            @RequestParam(required = false) @Parameter(description = "Specific channels to unpublish from") String[] channels) {
        ContentResponse content = contentService.unpublishContent(id, channels);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/my-content")
    @Operation(summary = "Get content created by current user (Operator)")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<ContentResponse>> getMyContent(
            @RequestParam(required = false) @Parameter(description = "Filter by content status (RASCUNHO, PUBLICADO)") ContentStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by channel") String channel,
            Pageable pageable) {
        Page<ContentResponse> content = contentService.getMyContent(status, channel, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/published")
    @Operation(summary = "Get all published content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Page<ContentResponse>> getPublishedContent(
            @RequestParam(required = false) @Parameter(description = "Filter by channel") String channel,
            Pageable pageable) {
        Page<ContentResponse> content = contentService.getPublishedContent(channel, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Page<ContentResponse>> getScheduledContent(Pageable pageable) {
        Page<ContentResponse> content = contentService.getScheduledContent(pageable);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/{id}/schedule")
    @Operation(summary = "Schedule content for future publication")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentResponse> scheduleContent(
            @PathVariable UUID id,
            @RequestParam @Parameter(description = "Scheduled publication date (ISO format)") String scheduledDate,
            @RequestParam @Parameter(description = "Channels to publish to") String[] channels) {
        ContentResponse content = contentService.scheduleContent(id, scheduledDate, channels);
        return ResponseEntity.ok(content);
    }
}
