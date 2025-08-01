package com.organixui.organixbackend.draft.service;

import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.draft.dto.CreateDraftRequest;
import com.organixui.organixbackend.draft.dto.DraftResponse;
import com.organixui.organixbackend.draft.dto.UpdateDraftRequest;
import com.organixui.organixbackend.draft.model.Draft;
import com.organixui.organixbackend.draft.model.DraftStatus;
import com.organixui.organixbackend.draft.repository.DraftRepository;
import com.organixui.organixbackend.product.model.Product;
import com.organixui.organixbackend.product.repository.ProductRepository;
import com.organixui.organixbackend.user.model.AdminType;
import com.organixui.organixbackend.user.model.User;
import com.organixui.organixbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de rascunhos.
 * Inclui operações CRUD com controle de acesso baseado em roles e ownership.
 */
@Service
@RequiredArgsConstructor
public class DraftService {
    
    private final DraftRepository draftRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    /**
     * Lista rascunhos baseado no role do usuário.
     * ADMIN: vê todos os rascunhos da empresa
     * OPERATOR: vê apenas seus próprios rascunhos
     */
    public List<DraftResponse> getAllDrafts(String status, UUID productId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User currentUser = getCurrentUser();
        
        List<Draft> drafts;
        
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            // Admin vê todos os rascunhos da empresa
            if (status != null && productId != null) {
                drafts = draftRepository.findByStatusAndProductIdAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), productId, companyId);
            } else if (status != null) {
                drafts = draftRepository.findByStatusAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), companyId);
            } else if (productId != null) {
                drafts = draftRepository.findByProductIdAndCompanyId(productId, companyId);
            } else {
                drafts = draftRepository.findByCompanyId(companyId);
            }
        } else {
            // Operator vê apenas seus próprios rascunhos
            String currentUsername = SecurityUtils.getCurrentUsername();
            if (status != null && productId != null) {
                drafts = draftRepository.findByStatusAndProductIdAndCreatedByAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), productId, currentUsername, companyId);
            } else if (status != null) {
                drafts = draftRepository.findByStatusAndCreatedByAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), currentUsername, companyId);
            } else if (productId != null) {
                drafts = draftRepository.findByProductIdAndCreatedByAndCompanyId(productId, currentUsername, companyId);
            } else {
                drafts = draftRepository.findByCreatedByAndCompanyId(currentUsername, companyId);
            }
        }
        
        return drafts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca rascunho por ID com verificação de permissão.
     */
    public DraftResponse getDraftById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.draft(id.toString()));
        
        // Verifica se o usuário tem permissão para ver este rascunho
        validateDraftAccess(draft);
        
        return convertToResponse(draft);
    }
    
    /**
     * Cria um novo rascunho.
     */
    @Transactional
    public DraftResponse createDraft(CreateDraftRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Verificar se o produto existe e pertence à empresa
        productRepository.findByIdAndCompanyId(request.getProductId(), companyId)
                .orElseThrow(() -> ResourceNotFoundException.product());
        
        Draft draft = new Draft();
        draft.setTitle(request.getTitle());
        draft.setContent(request.getContent());
        draft.setProductId(request.getProductId());
        draft.setCreatedBy(currentUser.getEmail());
        draft.setCompanyId(companyId);
        draft.setStatus(DraftStatus.DRAFT);
        
        draft = draftRepository.save(draft);
        return convertToResponse(draft);
    }
    
    /**
     * Atualiza um rascunho existente.
     */
    @Transactional
    public DraftResponse updateDraft(UUID id, UpdateDraftRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.draft(id.toString()));
        
        // Verifica se o usuário tem permissão para editar este rascunho
        validateDraftEditAccess(draft);
        
        draft.setTitle(request.getTitle());
        draft.setContent(request.getContent());
        
        // Apenas ADMIN pode alterar status via update
        if (request.getStatus() != null) {
            User currentUser = getCurrentUser();
            if (currentUser.getAdminType() == AdminType.ADMIN) {
                draft.setStatus(request.getStatus());
            }
        }
        
        draft = draftRepository.save(draft);
        return convertToResponse(draft);
    }
    
    /**
     * Exclui um rascunho.
     */
    @Transactional
    public void deleteDraft(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.draft(id.toString()));
        
        // Verifica se o usuário tem permissão para excluir este rascunho
        validateDraftEditAccess(draft);
        
        draftRepository.delete(draft);
    }
    
    /**
     * Altera o status de um rascunho (apenas ADMIN).
     */
    @Transactional
    public DraftResponse updateDraftStatus(UUID id, String status) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User currentUser = getCurrentUser();
        
        if (currentUser.getAdminType() != AdminType.ADMIN) {
            throw new BusinessException("Apenas administradores podem alterar o status de rascunhos");
        }
        
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.draft(id.toString()));
        
        try {
            draft.setStatus(DraftStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + status);
        }
        
        draft = draftRepository.save(draft);
        return convertToResponse(draft);
    }
    
    /**
     * Valida se o usuário tem acesso para visualizar o rascunho.
     */
    private void validateDraftAccess(Draft draft) {
        User currentUser = getCurrentUser();
        
        // ADMIN pode ver todos os rascunhos da empresa
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            return;
        }
        
        // OPERATOR só pode ver seus próprios rascunhos
        if (!draft.getCreatedBy().equals(currentUser.getEmail())) {
            throw new BusinessException("Você não tem permissão para acessar este rascunho");
        }
    }
    
    /**
     * Valida se o usuário tem acesso para editar/excluir o rascunho.
     */
    private void validateDraftEditAccess(Draft draft) {
        User currentUser = getCurrentUser();
        
        // ADMIN pode editar todos os rascunhos da empresa
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            return;
        }
        
        // OPERATOR só pode editar seus próprios rascunhos
        if (!draft.getCreatedBy().equals(currentUser.getEmail())) {
            throw new BusinessException("Você não tem permissão para editar este rascunho");
        }
        
        // Não pode editar rascunhos que já foram aprovados ou rejeitados
        if (draft.getStatus() == DraftStatus.APPROVED || draft.getStatus() == DraftStatus.REJECTED) {
            throw new BusinessException("Não é possível editar rascunhos com status " + draft.getStatus());
        }
    }
    
    /**
     * Obtém o usuário atual autenticado.
     */
    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }
    
    /**
     * Converte uma entidade Draft para DTO de resposta.
     */
    private DraftResponse convertToResponse(Draft draft) {
        Product product = productRepository.findById(draft.getProductId()).orElse(null);
        User user = userRepository.findByEmail(draft.getCreatedBy()).orElse(null);
        
        return new DraftResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getContent(),
                draft.getStatus(),
                draft.getProductId(),
                product != null ? product.getName() : null,
                user != null ? user.getId() : null,
                user != null ? user.getName() : null,
                draft.getCompanyId(),
                draft.getCreatedAt(),
                draft.getUpdatedAt()
        );
    }
}
