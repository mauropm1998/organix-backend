package com.organixui.organixbackend.performance.controller;

import com.organixui.organixbackend.performance.dto.ContentMetricsRequest;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.dto.PerformanceReportResponse;
import com.organixui.organixbackend.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
@Tag(name = "Performance", description = "Content performance and metrics endpoints")
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping("/metrics")
    @Operation(summary = "Update content metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentMetricsResponse> updateMetrics(@Valid @RequestBody ContentMetricsRequest request) {
        ContentMetricsResponse metrics = performanceService.updateMetrics(request);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/{contentId}")
    @Operation(summary = "Get metrics for specific content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ContentMetricsResponse> getContentMetrics(@PathVariable UUID contentId) {
        ContentMetricsResponse metrics = performanceService.getContentMetrics(contentId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get all metrics with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Page<ContentMetricsResponse>> getAllMetrics(
            @RequestParam(required = false) @Parameter(description = "Filter by content ID") UUID contentId,
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Start date for filtering") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "End date for filtering") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<ContentMetricsResponse> metrics = performanceService.getAllMetrics(contentId, productId, startDate, endDate, pageable);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/report/summary")
    @Operation(summary = "Get performance summary report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<PerformanceReportResponse> getPerformanceSummary(
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Start date for report") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "End date for report") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        PerformanceReportResponse report = performanceService.getPerformanceSummary(productId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/report/top-content")
    @Operation(summary = "Get top performing content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<ContentMetricsResponse>> getTopPerformingContent(
            @RequestParam(defaultValue = "views") @Parameter(description = "Metric to sort by") String metric,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of results") int limit,
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Start date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "End date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ContentMetricsResponse> topContent = performanceService.getTopPerformingContent(metric, limit, productId, startDate, endDate);
        return ResponseEntity.ok(topContent);
    }

    @GetMapping("/analytics/engagement")
    @Operation(summary = "Get engagement analytics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Map<String, Object>> getEngagementAnalytics(
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Start date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "End date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "daily") @Parameter(description = "Grouping period: daily, weekly, monthly") String period) {
        Map<String, Object> analytics = performanceService.getEngagementAnalytics(productId, startDate, endDate, period);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/trends")
    @Operation(summary = "Get performance trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Start date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "End date for analysis") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "views") @Parameter(description = "Metric to analyze") String metric) {
        Map<String, Object> trends = performanceService.getPerformanceTrends(productId, startDate, endDate, metric);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data with key metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId) {
        Map<String, Object> dashboard = performanceService.getDashboardData(productId);
        return ResponseEntity.ok(dashboard);
    }

    @DeleteMapping("/metrics/{contentId}")
    @Operation(summary = "Delete metrics for content (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContentMetrics(@PathVariable UUID contentId) {
        performanceService.deleteContentMetrics(contentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/metrics/bulk-update")
    @Operation(summary = "Bulk update metrics for multiple content")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContentMetricsResponse>> bulkUpdateMetrics(
            @Valid @RequestBody List<ContentMetricsRequest> requests) {
        List<ContentMetricsResponse> metrics = performanceService.bulkUpdateMetrics(requests);
        return ResponseEntity.ok(metrics);
    }
}
