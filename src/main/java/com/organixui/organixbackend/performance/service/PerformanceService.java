package com.organixui.organixbackend.performance.service;

import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.content.model.TrafficType;
import com.organixui.organixbackend.content.repository.ContentRepository;
import com.organixui.organixbackend.performance.dto.*;
import com.organixui.organixbackend.performance.model.ContentMetrics;
import com.organixui.organixbackend.performance.repository.ContentMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service para operações relacionadas a performance e métricas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

    private final ContentMetricsRepository contentMetricsRepository;
    private final ContentRepository contentRepository;

    /**
     * Atualiza métricas de um conteúdo.
     */
    public ContentMetricsResponse updateContentMetrics(UUID contentId, ContentMetricsRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        // Verifica se o conteúdo existe e pertence à empresa
        contentRepository.findByIdAndCompanyId(contentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        // Busca ou cria métricas para o conteúdo
        ContentMetrics metrics = contentMetricsRepository.findByContentId(contentId)
                .orElse(new ContentMetrics());

        // Atualiza as métricas
        metrics.setContentId(contentId);
        metrics.setViews(request.getViews());
        metrics.setLikes(request.getLikes());
        metrics.setReach(request.getReach());
        metrics.setEngagement(request.getEngagement());
        metrics.setComments(request.getComments());
        metrics.setShares(request.getShares());

        ContentMetrics savedMetrics = contentMetricsRepository.save(metrics);

        return ContentMetricsResponse.builder()
                .id(savedMetrics.getId())
                .contentId(savedMetrics.getContentId())
                .views(savedMetrics.getViews())
                .likes(savedMetrics.getLikes())
                .reach(savedMetrics.getReach())
                .engagement(savedMetrics.getEngagement())
                .comments(savedMetrics.getComments())
                .shares(savedMetrics.getShares())
                .build();
    }

    /**
     * Obtém métricas de um conteúdo específico.
     */
    public ContentMetricsResponse getContentMetrics(UUID contentId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        // Verifica se o conteúdo existe e pertence à empresa
        contentRepository.findByIdAndCompanyId(contentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        ContentMetrics metrics = contentMetricsRepository.findByContentId(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content metrics not found"));

        return ContentMetricsResponse.builder()
                .id(metrics.getId())
                .contentId(metrics.getContentId())
                .views(metrics.getViews())
                .likes(metrics.getLikes())
                .reach(metrics.getReach())
                .engagement(metrics.getEngagement())
                .comments(metrics.getComments())
                .shares(metrics.getShares())
                .build();
    }

    /**
     * Obtém resumo geral de performance da empresa com filtros opcionais.
     */
    public PerformanceSummaryResponse getPerformanceSummary(LocalDate startDate, LocalDate endDate, String channel, String productId) {
        return getPerformanceSummary(startDate, endDate, channel, productId, null);
    }

    /**
     * Obtém resumo geral de performance da empresa com filtros opcionais incluindo tipo de tráfego.
     */
    public PerformanceSummaryResponse getPerformanceSummary(LocalDate startDate, LocalDate endDate, String channel, String productId, TrafficType trafficType) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        log.debug("Fetching performance summary for company: {}", companyId);
        
        // Converte LocalDate para LocalDateTime para a consulta
        java.time.LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Converte productId string para UUID se fornecido
        UUID productUuid = null;
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid productId format: {}", productId);
                // Continua com productUuid = null, que fará a query ignorar o filtro
            }
        }
        
        // Busca métricas agregadas do banco de dados
        Object[] result = contentMetricsRepository.findAggregatedMetrics(
            companyId, startDateTime, endDateTime, channel, productUuid, trafficType);
        
        if (result == null || result.length < 6) {
            // Retorna valores zerados se não houver dados
            return PerformanceSummaryResponse.builder()
                    .totalViews(0L)
                    .totalLikes(0L)
                    .totalComments(0L)
                    .totalShares(0L)
                    .averageEngagementRate(0.0)
                    .totalReach(0L)
                    .build();
        }
        
        // Extrai os resultados da consulta
        Long totalViews = result[0] != null ? ((Number) result[0]).longValue() : 0L;
        Long totalLikes = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        Long totalComments = result[2] != null ? ((Number) result[2]).longValue() : 0L;
        Long totalShares = result[3] != null ? ((Number) result[3]).longValue() : 0L;
        Long totalReach = result[4] != null ? ((Number) result[4]).longValue() : 0L;
        Double averageEngagementRate = result[5] != null ? ((Number) result[5]).doubleValue() : 0.0;
        
        return PerformanceSummaryResponse.builder()
                .totalViews(totalViews)
                .totalLikes(totalLikes)
                .totalComments(totalComments)
                .totalShares(totalShares)
                .averageEngagementRate(averageEngagementRate)
                .totalReach(totalReach)
                .build();
    }

    /**
     * Obtém performance por canal.
     */
    public List<ChannelPerformanceResponse> getChannelPerformance() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        log.debug("Fetching channel performance for company: {}", companyId);
        
        // Busca performance por canal do banco de dados
        List<Object[]> results = contentMetricsRepository.findChannelPerformance(companyId);
        
        return results.stream().map(result -> {
            String channelName = (String) result[0];
            Long totalPosts = result[1] != null ? ((Number) result[1]).longValue() : 0L;
            Long totalViews = result[2] != null ? ((Number) result[2]).longValue() : 0L;
            Long totalLikes = result[3] != null ? ((Number) result[3]).longValue() : 0L;
            Long totalComments = result[4] != null ? ((Number) result[4]).longValue() : 0L;
            Long totalShares = result[5] != null ? ((Number) result[5]).longValue() : 0L;
            Double engagementRate = result[6] != null ? ((Number) result[6]).doubleValue() : 0.0;
            Double averageReach = result[7] != null ? ((Number) result[7]).doubleValue() : 0.0;
            
            return ChannelPerformanceResponse.builder()
                    .channelName(channelName)
                    .totalPosts(totalPosts)
                    .totalViews(totalViews)
                    .totalLikes(totalLikes)
                    .totalComments(totalComments)
                    .totalShares(totalShares)
                    .engagementRate(engagementRate)
                    .averageReach(averageReach)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtém top conteúdos por performance com filtros opcionais (sem paginação).
     */
    public List<TopContentResponse> getTopContent(LocalDate startDate, LocalDate endDate, String channel, String productId) {
        return getTopContent(startDate, endDate, channel, productId, null);
    }

    /**
     * Obtém top conteúdos por performance com filtros opcionais incluindo tipo de tráfego (sem paginação).
     */
    public List<TopContentResponse> getTopContent(LocalDate startDate, LocalDate endDate, String channel, String productId, TrafficType trafficType) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        log.debug("Fetching top content for company: {}", companyId);
        
        // Converte LocalDate para LocalDateTime para a consulta
        java.time.LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Converte productId string para UUID se fornecido
        UUID productUuid = null;
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid productId format: {}", productId);
            }
        }
        
        // Busca top conteúdos do banco de dados
        List<Object[]> results = contentMetricsRepository.findTopContent(
            companyId, startDateTime, endDateTime, channel, productUuid, trafficType);
        
        return results.stream().map(result -> {
            String contentId = result[0] != null ? result[0].toString() : "";
            String contentName = (String) result[1];
            String contentType = (String) result[2];
            String contentProductId = result[3] != null ? result[3].toString() : null;
            String productName = (String) result[4];
            String channelName = (String) result[5];
            Long channelLikes = result[6] != null ? ((Number) result[6]).longValue() : 0L;
            Long channelComments = result[7] != null ? ((Number) result[7]).longValue() : 0L;
            Long channelShares = result[8] != null ? ((Number) result[8]).longValue() : 0L;
            Long channelSiteVisits = result[9] != null ? ((Number) result[9]).longValue() : 0L;
            Long channelNewAccounts = result[10] != null ? ((Number) result[10]).longValue() : 0L;
            Long channelPostClicks = result[11] != null ? ((Number) result[11]).longValue() : 0L;
            String publishDate = result[12] != null ? result[12].toString() : "";
            Double performanceScore = result[13] != null ? ((Number) result[13]).doubleValue() : 0.0;
            
            return TopContentResponse.builder()
                    .contentId(contentId)
                    .contentName(contentName)
                    .contentType(contentType)
                    .productId(contentProductId)
                    .productName(productName)
                    .channel(channelName)
                    .channelLikes(channelLikes)
                    .channelComments(channelComments)
                    .channelShares(channelShares)
                    .channelSiteVisits(channelSiteVisits)
                    .channelNewAccounts(channelNewAccounts)
                    .channelPostClicks(channelPostClicks)
                    .publishDate(publishDate)
                    .performanceScore(performanceScore)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtém top conteúdos por performance com filtros opcionais.
     */
    public Page<TopContentResponse> getTopContent(Pageable pageable, LocalDate startDate, LocalDate endDate, String channel, String productId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        log.debug("Fetching top content for company: {}", companyId);
        
        // Converte LocalDate para LocalDateTime para a consulta
        java.time.LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Converte productId string para UUID se fornecido
        UUID productUuid = null;
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid productId format: {}", productId);
            }
        }
        
        // Busca top conteúdos do banco de dados
        List<Object[]> results = contentMetricsRepository.findTopContent(
            companyId, startDateTime, endDateTime, channel, productUuid);
        
        List<TopContentResponse> content = results.stream().map(result -> {
            String contentId = result[0] != null ? result[0].toString() : "";
            String contentName = (String) result[1];
            String contentType = (String) result[2];
            String contentProductId = result[3] != null ? result[3].toString() : null;
            String productName = (String) result[4];
            String channelName = (String) result[5];
            Long channelLikes = result[6] != null ? ((Number) result[6]).longValue() : 0L;
            Long channelComments = result[7] != null ? ((Number) result[7]).longValue() : 0L;
            Long channelShares = result[8] != null ? ((Number) result[8]).longValue() : 0L;
            Long channelSiteVisits = result[9] != null ? ((Number) result[9]).longValue() : 0L;
            Long channelNewAccounts = result[10] != null ? ((Number) result[10]).longValue() : 0L;
            Long channelPostClicks = result[11] != null ? ((Number) result[11]).longValue() : 0L;
            String publishDate = result[12] != null ? result[12].toString() : "";
            Double performanceScore = result[13] != null ? ((Number) result[13]).doubleValue() : 0.0;
            
            return TopContentResponse.builder()
                    .contentId(contentId)
                    .contentName(contentName)
                    .contentType(contentType)
                    .productId(contentProductId)
                    .productName(productName)
                    .channel(channelName)
                    .channelLikes(channelLikes)
                    .channelComments(channelComments)
                    .channelShares(channelShares)
                    .channelSiteVisits(channelSiteVisits)
                    .channelNewAccounts(channelNewAccounts)
                    .channelPostClicks(channelPostClicks)
                    .publishDate(publishDate)
                    .performanceScore(performanceScore)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
        
        // Aplica paginação manual já que a query não suporta Pageable diretamente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), content.size());
        
        List<TopContentResponse> paginatedContent = start >= content.size() ? 
            java.util.Collections.emptyList() : 
            content.subList(start, end);
        
        return new PageImpl<>(paginatedContent, pageable, content.size());
    }
}
