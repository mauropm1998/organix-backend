package com.organixui.organixbackend.company.service;

import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.company.dto.CompanyResponse;
import com.organixui.organixbackend.company.dto.CompanyStatsResponse;
import com.organixui.organixbackend.company.dto.UpdateCompanyRequest;
import com.organixui.organixbackend.company.model.Company;
import com.organixui.organixbackend.company.repository.CompanyRepository;
import com.organixui.organixbackend.content.repository.ContentRepository;
import com.organixui.organixbackend.draft.model.DraftStatus;
import com.organixui.organixbackend.draft.repository.DraftRepository;
import com.organixui.organixbackend.product.repository.ProductRepository;
import com.organixui.organixbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço responsável pela gestão de empresas.
 * Inclui operações de consulta e atualização dos dados da empresa.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DraftRepository draftRepository;
    private final ContentRepository contentRepository;
    
    /**
     * Busca os dados da empresa do usuário autenticado.
     */
    public CompanyResponse getCompanyInfo() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> ResourceNotFoundException.company(companyId.toString()));
        
        return convertToResponse(company);
    }
    
    /**
     * Atualiza os dados da empresa do usuário autenticado.
     */
    @Transactional
    public CompanyResponse updateCompany(UpdateCompanyRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> ResourceNotFoundException.company(companyId.toString()));
        
        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setSize(request.getSize());
        company.setWebsite(request.getWebsite());
        company.setDescription(request.getDescription());
        
        company = companyRepository.save(company);
        return convertToResponse(company);
    }
    
    /**
     * Retorna estatísticas da empresa.
     */
    public CompanyStatsResponse getCompanyStats(UUID companyId) {
        // Obter dados básicos da empresa
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        
        // Contar estatísticas
        long totalUsers = userRepository.countByCompanyId(companyId);
        long totalProducts = productRepository.countByCompanyId(companyId);
        long totalDrafts = draftRepository.countByCompanyId(companyId);
        long pendingDrafts = draftRepository.countByCompanyIdAndStatus(companyId, DraftStatus.REVIEW);
        long approvedDrafts = draftRepository.countByCompanyIdAndStatus(companyId, DraftStatus.APPROVED);
        long totalContent = contentRepository.countByCompanyId(companyId);
        long publishedContent = contentRepository.countByCompanyIdAndPublished(companyId, true);
        long scheduledContent = contentRepository.countByCompanyIdAndScheduledDateAfter(companyId, LocalDateTime.now());
        
        return CompanyStatsResponse.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalDrafts(totalDrafts)
                .pendingDrafts(pendingDrafts)
                .approvedDrafts(approvedDrafts)
                .totalContent(totalContent)
                .publishedContent(publishedContent)
                .scheduledContent(scheduledContent)
                .build();
    }    /**
     * Converte uma entidade Company para DTO de resposta.
     */
    private CompanyResponse convertToResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getSize(),
                company.getWebsite(),
                company.getDescription(),
                company.getAdminId(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
