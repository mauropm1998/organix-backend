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

    /**
     * Lista rascunhos baseado no role do usuário.
     * ADMIN: vê todos os rascunhos da empresa
     * OPERATOR: vê apenas seus próprios rascunhos
     */
    public List<DraftResponse> getAllDrafts(String status) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = getCurrentUser();
        
        List<Draft> drafts;
        
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            // Admin vê todos os rascunhos da empresa
            if (status != null) {
                drafts = draftRepository.findByStatusAndCompanyId(DraftStatus.valueOf(status.toUpperCase()), companyId);
            } else {
                drafts = draftRepository.findByCompanyId(companyId);
            }
        } else {
            // Operator vê apenas seus próprios rascunhos
            if (status != null) {
                drafts = draftRepository.findByStatusAndCreatorId(DraftStatus.valueOf(status.toUpperCase()), currentUserId);
            } else {
                drafts = draftRepository.findByCreatorId(currentUserId);
            }
        }
        
        return drafts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtém um rascunho específico pelo ID.
     * Valida se o usuário tem permissão para ver o rascunho.
     */
    public DraftResponse getDraftById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Draft draft = draftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rascunho não encontrado"));
        
        validateUserCanAccessDraft(draft);
        
        return convertToResponse(draft);
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

    /**
     * Valida se o usuário atual pode acessar o rascunho.
     */
    private void validateUserCanAccessDraft(Draft draft) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = getCurrentUser();
        
        // Admin pode acessar qualquer rascunho da empresa
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            return;
        }
        
        // Operator só pode acessar seus próprios rascunhos
        if (!draft.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("Você não tem permissão para acessar este rascunho");
        }
    }

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
        
        return new DraftResponse(
                draft.getId(),
                draft.getName(),
                draft.getType(),
                draft.getCreatorId(),
                creator != null ? creator.getName() : "Unknown",
                draft.getContent(),
                draft.getStatus(),
                draft.getCreatedAt(),
                draft.getCompanyId()
        );
    }
}
