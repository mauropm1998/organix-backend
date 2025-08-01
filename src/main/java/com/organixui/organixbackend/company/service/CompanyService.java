package com.organixui.organixbackend.company.service;

import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.company.dto.CompanyResponse;
import com.organixui.organixbackend.company.dto.UpdateCompanyRequest;
import com.organixui.organixbackend.company.model.Company;
import com.organixui.organixbackend.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço responsável pela gestão de empresas.
 * Inclui operações de consulta e atualização dos dados da empresa.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    
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
