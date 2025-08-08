package com.organixui.organixbackend.company.controller;

import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.company.dto.CompanyResponse;
import com.organixui.organixbackend.company.dto.CompanyStatsResponse;
import com.organixui.organixbackend.company.dto.UpdateCompanyRequest;
import com.organixui.organixbackend.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gerenciamento de empresa.
 * Inclui operações de consulta e atualização dos dados da empresa.
 */
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "Operações de gerenciamento da empresa")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {
    
    private final CompanyService companyService;
    
    @GetMapping
    @Operation(summary = "Buscar informações da empresa", description = "Retorna as informações da empresa do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações da empresa retornadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<CompanyResponse> getCompanyInfo() {
        CompanyResponse company = companyService.getCompanyInfo();
        return ResponseEntity.ok(company);
    }
    
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar informações da empresa", description = "Atualiza as informações da empresa (apenas administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem atualizar a empresa")
    })
    public ResponseEntity<CompanyResponse> updateCompany(
            @Parameter(description = "Novos dados da empresa") @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse company = companyService.updateCompany(request);
        return ResponseEntity.ok(company);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Estatísticas da empresa", description = "Retorna estatísticas gerais da empresa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<CompanyStatsResponse> getCompanyStats() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        CompanyStatsResponse stats = companyService.getCompanyStats(companyId);
        return ResponseEntity.ok(stats);
    }
}
