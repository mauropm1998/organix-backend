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
import com.organixui.organixbackend.user.model.AdminType;
import com.organixui.organixbackend.user.model.User;
import com.organixui.organixbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final com.organixui.organixbackend.product.repository.ProductRepository productRepository;

    /**
     * Lista rascunhos baseado no role do usuário.
     * ADMIN e OPERATOR: vê todos os rascunhos da empresa (OPERATOR somente leitura)
     */
    public List<DraftResponse> getAllDrafts(String status) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        List<Draft> drafts;
    java.time.LocalDateTime fromDate = java.time.LocalDateTime.now().minusDays(7);
        // Ambos veem todos os rascunhos da empresa
        if (status != null) {
            drafts = draftRepository.findByStatusAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), companyId);
        } else {
            drafts = draftRepository.findByCompanyId(companyId);
        }
    // Filtra últimos 7 dias em memória (consultas simples existentes)
    drafts = drafts.stream()
        .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().isAfter(fromDate))
        .sorted(java.util.Comparator.comparing(Draft::getCreatedAt).reversed())
        .collect(Collectors.toList());
        
        return drafts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<DraftResponse> getAllDrafts(String status, UUID creatorId, UUID productId, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        DraftStatus st = null;
        if (status != null) {
            try { st = DraftStatus.valueOf(status.toUpperCase()); } catch (IllegalArgumentException e) { throw new BusinessException("Status inválido"); }
        }
    java.time.LocalDateTime fromDate = java.time.LocalDateTime.now().minusDays(7);
    Page<Draft> page = draftRepository.searchDrafts(companyId, st, creatorId, productId, fromDate, pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * Obtém um rascunho específico pelo ID.
     * Valida se o usuário tem permissão para ver o rascunho.
     */
    public DraftResponse getDraftById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        
    // Operador agora pode visualizar qualquer rascunho da empresa (somente leitura)
        
        return convertToResponse(draft);
    }

    public List<DraftResponse> getRecentDrafts(int days) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        java.time.LocalDateTime from = java.time.LocalDateTime.now().minusDays(days);
        return draftRepository.findByCompanyIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(companyId, from)
                .stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public com.organixui.organixbackend.draft.dto.DraftStatsResponse getDraftStats() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        long total = draftRepository.countByCompanyId(companyId);
        long approved = draftRepository.countByCompanyIdAndStatus(companyId, DraftStatus.APPROVED);
        return com.organixui.organixbackend.draft.dto.DraftStatsResponse.builder()
                .total(total)
                .approved(approved)
                .build();
    }

    /**
     * Cria um novo rascunho.
     */
    @Transactional
    public DraftResponse createDraft(CreateDraftRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
    Draft draft = new Draft();
    draft.setName(request.getName());
    draft.setType(request.getType());
    draft.setProductId(request.getProductId());
    draft.setContent(request.getContent());
        draft.setCreatorId(currentUserId);
        draft.setCompanyId(companyId);
        draft.setStatus(request.getStatus() != null ? request.getStatus() : DraftStatus.PENDING);
        
        Draft savedDraft = draftRepository.save(draft);
        return convertToResponse(savedDraft);
    }

    /**
     * Atualiza um rascunho existente.
     * Apenas o criador ou admin pode atualizar.
     */
    @Transactional
    public DraftResponse updateDraft(UUID id, UpdateDraftRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        
        validateUserCanModifyDraft(draft);
        
        if (request.getName() != null) {
            draft.setName(request.getName());
        }
        if (request.getType() != null) {
            draft.setType(request.getType());
        }
        if (request.getProductId() != null) {
            draft.setProductId(request.getProductId());
        }
        if (request.getContent() != null) {
            draft.setContent(request.getContent());
        }
        if (request.getStatus() != null) {
            draft.setStatus(request.getStatus());
        }
        
        Draft savedDraft = draftRepository.save(draft);
        return convertToResponse(savedDraft);
    }

    /**
     * Exclui um rascunho.
     * Apenas o criador ou admin pode excluir.
     */
    @Transactional
    public void deleteDraft(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        
        validateUserCanModifyDraft(draft);
        
        draftRepository.delete(draft);
    }

    // Removida validação restritiva de acesso para leitura; mantida restrição para modificar

    /**
     * Valida se o usuário atual pode modificar o rascunho.
     */
    private void validateUserCanModifyDraft(Draft draft) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = getCurrentUser();
        
        // Admin pode modificar qualquer rascunho da empresa
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            return;
        }
        
        // Operator só pode modificar seus próprios rascunhos
        if (!draft.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("Você não tem permissão para modificar este rascunho");
        }
    }

    /**
     * Obtém o usuário atual.
     */
    private User getCurrentUser() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    /**
     * Converte Draft entity para DraftResponse DTO.
     */
    private DraftResponse convertToResponse(Draft draft) {
        User creator = userRepository.findById(draft.getCreatorId()).orElse(null);
        
    String productName = null;
    if (draft.getProductId() != null) {
        productName = productRepository.findById(draft.getProductId()).map(p -> p.getName()).orElse(null);
    }

    return DraftResponse.builder()
        .id(draft.getId())
        .name(draft.getName())
        .type(draft.getType())
        .productId(draft.getProductId())
        .productName(productName)
        .creatorId(draft.getCreatorId())
        .creatorName(creator != null ? creator.getName() : "Unknown")
        .content(draft.getContent())
        .status(draft.getStatus())
        .createdAt(draft.getCreatedAt())
        .companyId(draft.getCompanyId())
        .build();
    }
}
