package com.organixui.organixbackend.performance.service;

import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.security.SecurityUtils;
import com.organixui.organixbackend.content.model.Content;
import com.organixui.organixbackend.content.repository.ContentRepository;
import com.organixui.organixbackend.performance.dto.ContentMetricsRequest;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.dto.PerformanceReportResponse;
import com.organixui.organixbackend.performance.model.ContentMetrics;
import com.organixui.organixbackend.performance.repository.ContentMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PerformanceService {

    private final ContentMetricsRepository contentMetricsRepository;
    private final ContentRepository contentRepository;

    public ContentMetricsResponse updateMetrics(ContentMetricsRequest request) {
        log.info("Updating metrics for content: {}", request.getContentId());
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        // Verificar se o conteúdo existe e pertence à empresa
        Content content = contentRepository.findByIdAndCompanyId(request.getContentId(), companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso ao conteúdo
        validateContentAccess(content, userEmail);
        
        // Buscar métricas existentes ou criar novas
        ContentMetrics metrics = contentMetricsRepository.findByContentId(request.getContentId())
                .orElseGet(() -> {
                    ContentMetrics newMetrics = new ContentMetrics();
                    newMetrics.setId(UUID.randomUUID());
                    newMetrics.setContentId(request.getContentId());
                    newMetrics.setCompanyId(companyId);
                    newMetrics.setCreatedAt(LocalDateTime.now());
                    return newMetrics;
                });
        
        // Atualizar métricas
        if (request.getViews() != null) {
            metrics.setViews(request.getViews());
        }
        if (request.getLikes() != null) {
            metrics.setLikes(request.getLikes());
        }
        if (request.getShares() != null) {
            metrics.setShares(request.getShares());
        }
        if (request.getComments() != null) {
            metrics.setComments(request.getComments());
        }
        if (request.getReach() != null) {
            metrics.setReach(request.getReach());
        }
        if (request.getImpressions() != null) {
            metrics.setImpressions(request.getImpressions());
        }
        if (request.getEngagementRate() != null) {
            metrics.setEngagementRate(request.getEngagementRate());
        }
        if (request.getClickThroughRate() != null) {
            metrics.setClickThroughRate(request.getClickThroughRate());
        }
        if (request.getConversionRate() != null) {
            metrics.setConversionRate(request.getConversionRate());
        }
        if (request.getMetricsData() != null) {
            metrics.setMetricsData(request.getMetricsData());
        }
        
        metrics.setUpdatedAt(LocalDateTime.now());
        
        ContentMetrics savedMetrics = contentMetricsRepository.save(metrics);
        
        log.info("Metrics updated successfully for content: {}", request.getContentId());
        return mapToResponse(savedMetrics);
    }

    @Transactional(readOnly = true)
    public ContentMetricsResponse getContentMetrics(UUID contentId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        // Verificar se o conteúdo existe e pertence à empresa
        Content content = contentRepository.findByIdAndCompanyId(contentId, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso ao conteúdo
        validateContentAccess(content, userEmail);
        
        ContentMetrics metrics = contentMetricsRepository.findByContentId(contentId)
                .orElseThrow(() -> new BusinessException("Metrics not found for content: " + contentId));
        
        return mapToResponse(metrics);
    }

    @Transactional(readOnly = true)
    public Page<ContentMetricsResponse> getAllMetrics(UUID contentId, UUID productId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        if (isAdmin()) {
            // Admin vê todas as métricas da empresa
            return contentMetricsRepository.findByCompanyIdWithFilters(companyId, contentId, productId, startDateTime, endDateTime, pageable)
                    .map(this::mapToResponse);
        } else {
            // Operator vê apenas métricas dos próprios conteúdos
            return contentMetricsRepository.findByCompanyIdAndCreatedByWithFilters(companyId, userEmail, contentId, productId, startDateTime, endDateTime, pageable)
                    .map(this::mapToResponse);
        }
    }

    @Transactional(readOnly = true)
    public PerformanceReportResponse getPerformanceSummary(UUID productId, LocalDate startDate, LocalDate endDate) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        // Buscar métricas baseado no nível de acesso
        List<ContentMetrics> metrics;
        if (isAdmin()) {
            metrics = contentMetricsRepository.findMetricsForReport(companyId, productId, startDateTime, endDateTime);
        } else {
            metrics = contentMetricsRepository.findMetricsForReportByUser(companyId, userEmail, productId, startDateTime, endDateTime);
        }
        
        PerformanceReportResponse report = new PerformanceReportResponse();
        
        // Calcular resumo
        PerformanceReportResponse.PerformanceSummary summary = calculateSummary(metrics);
        report.setSummary(summary);
        
        // Métricas por período
        Map<String, Object> periodMetrics = calculatePeriodMetrics(metrics, startDate, endDate);
        report.setPeriodMetrics(periodMetrics);
        
        // Comparação com período anterior
        Map<String, Object> comparison = calculateComparison(companyId, userEmail, productId, startDate, endDate);
        report.setComparison(comparison);
        
        // Top conteúdos
        Map<String, Object> topContent = calculateTopContent(metrics);
        report.setTopContent(topContent);
        
        return report;
    }

    @Transactional(readOnly = true)
    public List<ContentMetricsResponse> getTopPerformingContent(String metric, int limit, UUID productId, LocalDate startDate, LocalDate endDate) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, metric));
        
        Page<ContentMetrics> metricsPage;
        if (isAdmin()) {
            metricsPage = contentMetricsRepository.findByCompanyIdWithFilters(companyId, null, productId, startDateTime, endDateTime, pageable);
        } else {
            metricsPage = contentMetricsRepository.findByCompanyIdAndCreatedByWithFilters(companyId, userEmail, null, productId, startDateTime, endDateTime, pageable);
        }
        
        return metricsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEngagementAnalytics(UUID productId, LocalDate startDate, LocalDate endDate, String period) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        List<ContentMetrics> metrics;
        if (isAdmin()) {
            metrics = contentMetricsRepository.findMetricsForReport(companyId, productId, startDateTime, endDateTime);
        } else {
            metrics = contentMetricsRepository.findMetricsForReportByUser(companyId, userEmail, productId, startDateTime, endDateTime);
        }
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Agrupar por período
        Map<String, List<ContentMetrics>> groupedMetrics = groupMetricsByPeriod(metrics, period);
        
        // Calcular médias de engajamento por período
        Map<String, Double> engagementByPeriod = groupedMetrics.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(m -> m.getEngagementRate() != null ? m.getEngagementRate() : 0.0)
                                .average()
                                .orElse(0.0)
                ));
        
        analytics.put("engagementByPeriod", engagementByPeriod);
        analytics.put("totalMetrics", metrics.size());
        analytics.put("averageEngagement", metrics.stream()
                .mapToDouble(m -> m.getEngagementRate() != null ? m.getEngagementRate() : 0.0)
                .average()
                .orElse(0.0));
        
        return analytics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPerformanceTrends(UUID productId, LocalDate startDate, LocalDate endDate, String metric) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        List<ContentMetrics> metrics;
        if (isAdmin()) {
            metrics = contentMetricsRepository.findMetricsForReport(companyId, productId, startDateTime, endDateTime);
        } else {
            metrics = contentMetricsRepository.findMetricsForReportByUser(companyId, userEmail, productId, startDateTime, endDateTime);
        }
        
        Map<String, Object> trends = new HashMap<>();
        
        // Agrupar por dia e calcular trend
        Map<LocalDate, Double> dailyValues = metrics.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getUpdatedAt().toLocalDate(),
                        Collectors.averagingDouble(m -> getMetricValue(m, metric))
                ));
        
        trends.put("dailyTrends", dailyValues);
        trends.put("metric", metric);
        trends.put("period", Map.of("start", startDate, "end", endDate));
        
        return trends;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData(UUID productId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        
        List<ContentMetrics> metrics;
        if (isAdmin()) {
            metrics = contentMetricsRepository.findMetricsForReport(companyId, productId, last30Days, LocalDateTime.now());
        } else {
            metrics = contentMetricsRepository.findMetricsForReportByUser(companyId, userEmail, productId, last30Days, LocalDateTime.now());
        }
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // KPIs principais
        dashboard.put("totalViews", metrics.stream().mapToLong(m -> m.getViews() != null ? m.getViews() : 0).sum());
        dashboard.put("totalLikes", metrics.stream().mapToLong(m -> m.getLikes() != null ? m.getLikes() : 0).sum());
        dashboard.put("totalShares", metrics.stream().mapToLong(m -> m.getShares() != null ? m.getShares() : 0).sum());
        dashboard.put("totalComments", metrics.stream().mapToLong(m -> m.getComments() != null ? m.getComments() : 0).sum());
        dashboard.put("averageEngagement", metrics.stream()
                .mapToDouble(m -> m.getEngagementRate() != null ? m.getEngagementRate() : 0.0)
                .average()
                .orElse(0.0));
        
        // Conteúdo mais popular
        dashboard.put("topContent", metrics.stream()
                .sorted((a, b) -> Long.compare(
                        b.getViews() != null ? b.getViews() : 0,
                        a.getViews() != null ? a.getViews() : 0))
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
        
        return dashboard;
    }

    public void deleteContentMetrics(UUID contentId) {
        log.info("Deleting metrics for content: {}", contentId);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        // Verificar se o conteúdo existe e pertence à empresa
        Content content = contentRepository.findByIdAndCompanyId(contentId, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        contentMetricsRepository.deleteByContentId(contentId);
        
        log.info("Metrics deleted successfully for content: {}", contentId);
    }

    public List<ContentMetricsResponse> bulkUpdateMetrics(List<ContentMetricsRequest> requests) {
        log.info("Bulk updating metrics for {} contents", requests.size());
        
        List<ContentMetricsResponse> responses = new ArrayList<>();
        
        for (ContentMetricsRequest request : requests) {
            try {
                ContentMetricsResponse response = updateMetrics(request);
                responses.add(response);
            } catch (Exception e) {
                log.error("Error updating metrics for content: {}", request.getContentId(), e);
                // Continuar com os próximos mesmo se houver erro
            }
        }
        
        log.info("Bulk update completed. {} out of {} successful", responses.size(), requests.size());
        return responses;
    }

    private void validateContentAccess(Content content, String userEmail) {
        if (!isAdmin() && !content.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("Access denied to this content");
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));
    }

    private PerformanceReportResponse.PerformanceSummary calculateSummary(List<ContentMetrics> metrics) {
        PerformanceReportResponse.PerformanceSummary summary = new PerformanceReportResponse.PerformanceSummary();
        
        summary.setTotalViews(metrics.stream().mapToLong(m -> m.getViews() != null ? m.getViews() : 0).sum());
        summary.setTotalLikes(metrics.stream().mapToLong(m -> m.getLikes() != null ? m.getLikes() : 0).sum());
        summary.setTotalShares(metrics.stream().mapToLong(m -> m.getShares() != null ? m.getShares() : 0).sum());
        summary.setTotalComments(metrics.stream().mapToLong(m -> m.getComments() != null ? m.getComments() : 0).sum());
        summary.setTotalReach(metrics.stream().mapToLong(m -> m.getReach() != null ? m.getReach() : 0).sum());
        summary.setTotalImpressions(metrics.stream().mapToLong(m -> m.getImpressions() != null ? m.getImpressions() : 0).sum());
        
        summary.setAverageEngagementRate(metrics.stream()
                .mapToDouble(m -> m.getEngagementRate() != null ? m.getEngagementRate() : 0.0)
                .average()
                .orElse(0.0));
        
        summary.setAverageClickThroughRate(metrics.stream()
                .mapToDouble(m -> m.getClickThroughRate() != null ? m.getClickThroughRate() : 0.0)
                .average()
                .orElse(0.0));
        
        summary.setAverageConversionRate(metrics.stream()
                .mapToDouble(m -> m.getConversionRate() != null ? m.getConversionRate() : 0.0)
                .average()
                .orElse(0.0));
        
        summary.setTotalContent((long) metrics.size());
        
        return summary;
    }

    private Map<String, Object> calculatePeriodMetrics(List<ContentMetrics> metrics, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> periodMetrics = new HashMap<>();
        
        // Agrupar por dia
        Map<LocalDate, List<ContentMetrics>> dailyMetrics = metrics.stream()
                .collect(Collectors.groupingBy(m -> m.getUpdatedAt().toLocalDate()));
        
        periodMetrics.put("daily", dailyMetrics.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> calculateSummary(entry.getValue())
                )));
        
        return periodMetrics;
    }

    private Map<String, Object> calculateComparison(UUID companyId, String userEmail, UUID productId, LocalDate startDate, LocalDate endDate) {
        // Implementação simplificada - pode ser expandida
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("previousPeriod", "Not implemented yet");
        return comparison;
    }

    private Map<String, Object> calculateTopContent(List<ContentMetrics> metrics) {
        Map<String, Object> topContent = new HashMap<>();
        
        // Top por visualizações
        topContent.put("byViews", metrics.stream()
                .sorted((a, b) -> Long.compare(
                        b.getViews() != null ? b.getViews() : 0,
                        a.getViews() != null ? a.getViews() : 0))
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
        
        // Top por engajamento
        topContent.put("byEngagement", metrics.stream()
                .sorted((a, b) -> Double.compare(
                        b.getEngagementRate() != null ? b.getEngagementRate() : 0.0,
                        a.getEngagementRate() != null ? a.getEngagementRate() : 0.0))
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
        
        return topContent;
    }

    private Map<String, List<ContentMetrics>> groupMetricsByPeriod(List<ContentMetrics> metrics, String period) {
        switch (period.toLowerCase()) {
            case "daily":
                return metrics.stream()
                        .collect(Collectors.groupingBy(m -> m.getUpdatedAt().toLocalDate().toString()));
            case "weekly":
                return metrics.stream()
                        .collect(Collectors.groupingBy(m -> getWeekOfYear(m.getUpdatedAt())));
            case "monthly":
                return metrics.stream()
                        .collect(Collectors.groupingBy(m -> m.getUpdatedAt().getYear() + "-" + m.getUpdatedAt().getMonthValue()));
            default:
                return metrics.stream()
                        .collect(Collectors.groupingBy(m -> m.getUpdatedAt().toLocalDate().toString()));
        }
    }

    private String getWeekOfYear(LocalDateTime dateTime) {
        // Implementação simplificada
        return dateTime.getYear() + "-W" + ((dateTime.getDayOfYear() / 7) + 1);
    }

    private double getMetricValue(ContentMetrics metrics, String metric) {
        switch (metric.toLowerCase()) {
            case "views":
                return metrics.getViews() != null ? metrics.getViews().doubleValue() : 0.0;
            case "likes":
                return metrics.getLikes() != null ? metrics.getLikes().doubleValue() : 0.0;
            case "shares":
                return metrics.getShares() != null ? metrics.getShares().doubleValue() : 0.0;
            case "comments":
                return metrics.getComments() != null ? metrics.getComments().doubleValue() : 0.0;
            case "engagementrate":
                return metrics.getEngagementRate() != null ? metrics.getEngagementRate() : 0.0;
            case "reach":
                return metrics.getReach() != null ? metrics.getReach().doubleValue() : 0.0;
            case "impressions":
                return metrics.getImpressions() != null ? metrics.getImpressions().doubleValue() : 0.0;
            default:
                return 0.0;
        }
    }

    private ContentMetricsResponse mapToResponse(ContentMetrics metrics) {
        ContentMetricsResponse response = new ContentMetricsResponse();
        response.setId(metrics.getId());
        response.setContentId(metrics.getContentId());
        response.setCompanyId(metrics.getCompanyId());
        response.setViews(metrics.getViews());
        response.setLikes(metrics.getLikes());
        response.setShares(metrics.getShares());
        response.setComments(metrics.getComments());
        response.setEngagementRate(metrics.getEngagementRate());
        response.setReach(metrics.getReach());
        response.setImpressions(metrics.getImpressions());
        response.setClickThroughRate(metrics.getClickThroughRate());
        response.setConversionRate(metrics.getConversionRate());
        response.setMetricsData(metrics.getMetricsData());
        response.setCreatedAt(metrics.getCreatedAt());
        response.setUpdatedAt(metrics.getUpdatedAt());
        return response;
    }
}
