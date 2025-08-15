package com.organixui.organixbackend.performance.controller;

import com.organixui.organixbackend.performance.dto.PerformanceSummaryResponse;
import com.organixui.organixbackend.performance.dto.ChannelPerformanceResponse;
import com.organixui.organixbackend.performance.dto.TopContentResponse;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para métricas de performance.
 * ADMIN e OPERATOR podem acessar.
 */
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
@Tag(name = "Performance", description = "Performance metrics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PerformanceController {
    
    private final PerformanceService performanceService;
    
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Métricas agregadas", description = "Retorna métricas agregadas com filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas retornadas com sucesso")
    })
    public ResponseEntity<PerformanceSummaryResponse> getPerformanceSummary(
            @Parameter(description = "Data de início do período (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data de fim do período (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Nome do canal específico")
            @RequestParam(required = false) String channel,
            @Parameter(description = "ID do produto específico")
            @RequestParam(required = false) String productId) {
        PerformanceSummaryResponse summary = performanceService.getPerformanceSummary(startDate, endDate, channel, productId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/channel-performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Performance por canal", description = "Retorna métricas de performance por canal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Performance por canal retornada com sucesso")
    })
    public ResponseEntity<List<ChannelPerformanceResponse>> getChannelPerformance() {
        List<ChannelPerformanceResponse> channelPerformance = performanceService.getChannelPerformance();
        return ResponseEntity.ok(channelPerformance);
    }
    
    @GetMapping("/top-content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Conteúdo com melhor performance", description = "Retorna lista de conteúdo com melhor performance com filtros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top content retornado com sucesso")
    })
    public ResponseEntity<List<TopContentResponse>> getTopContent(
            @Parameter(description = "Data de início do período (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data de fim do período (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Nome do canal específico")
            @RequestParam(required = false) String channel,
            @Parameter(description = "ID do produto específico")
            @RequestParam(required = false) String productId) {
        List<TopContentResponse> topContent = performanceService.getTopContent(startDate, endDate, channel, productId);
        return ResponseEntity.ok(topContent);
    }
    
    @GetMapping("/content/{contentId}/metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Métricas de conteúdo específico", description = "Retorna métricas de performance de um conteúdo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas do conteúdo retornadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conteúdo ou métricas não encontradas")
    })
    public ResponseEntity<ContentMetricsResponse> getContentMetrics(
            @Parameter(description = "ID do conteúdo") @PathVariable UUID contentId) {
        ContentMetricsResponse metrics = performanceService.getContentMetrics(contentId);
        return ResponseEntity.ok(metrics);
    }
}
