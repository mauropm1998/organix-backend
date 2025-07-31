package com.organixui.organixbackend.content.service;

import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.security.SecurityUtils;
import com.organixui.organixbackend.content.dto.ContentRequest;
import com.organixui.organixbackend.content.dto.ContentResponse;
import com.organixui.organixbackend.content.model.Content;
import com.organixui.organixbackend.content.repository.ContentRepository;
import com.organixui.organixbackend.draft.model.Draft;
import com.organixui.organixbackend.draft.model.DraftStatus;
import com.organixui.organixbackend.draft.repository.DraftRepository;
import com.organixui.organixbackend.performance.model.ContentMetrics;
import com.organixui.organixbackend.performance.repository.ContentMetricsRepository;
import com.organixui.organixbackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final DraftRepository draftRepository;
    private final ProductRepository productRepository;
    private final ContentMetricsRepository contentMetricsRepository;

    public ContentResponse createContentFromDraft(UUID draftId) {
        log.info("Creating content from draft: {}", draftId);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        // Buscar o draft
        Draft draft = draftRepository.findByIdAndCompanyId(draftId, companyId)
                .orElseThrow(() -> ResourceNotFoundException.draft());
        
        // Validar se o draft está aprovado
        if (draft.getStatus() != DraftStatus.APPROVED) {
            throw new BusinessException("Only approved drafts can be converted to content");
        }
        
        // Validar acesso ao draft
        validateDraftAccess(draft, userEmail);
        
        // Criar content a partir do draft
        Content content = new Content();
        content.setId(UUID.randomUUID());
        content.setTitle(draft.getTitle());
        content.setDescription(draft.getDescription());
        content.setContent(draft.getContent());
        content.setProductId(draft.getProductId());
        content.setCompanyId(companyId);
        content.setCreatedBy(userEmail);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        content.setPublished(false);
        content.setChannels(new ArrayList<>());
        
        Content savedContent = contentRepository.save(content);
        
        // Criar métricas iniciais
        createInitialMetrics(savedContent);
        
        log.info("Content created successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    public ContentResponse createContent(ContentRequest request) {
        log.info("Creating content directly");
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        // Validar se o produto existe e pertence à empresa
        productRepository.findByIdAndCompanyId(request.getProductId(), companyId)
                .orElseThrow(() -> ResourceNotFoundException.product());
        
        Content content = new Content();
        content.setId(UUID.randomUUID());
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContent(request.getContent());
        content.setProductId(request.getProductId());
        content.setCompanyId(companyId);
        content.setCreatedBy(userEmail);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        content.setPublished(false);
        content.setChannels(request.getChannels() != null ? request.getChannels() : new ArrayList<>());
        content.setScheduledDate(request.getScheduledDate());
        
        Content savedContent = contentRepository.save(content);
        
        // Criar métricas iniciais
        createInitialMetrics(savedContent);
        
        log.info("Content created successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse> getAllContent(UUID productId, Boolean published, String channel, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        if (isAdmin()) {
            // Admin vê todo o conteúdo da empresa
            return contentRepository.findByCompanyIdWithFilters(
                    companyId.toString(), 
                    productId != null ? productId.toString() : null, 
                    published, 
                    channel, 
                    pageable)
                    .map(this::mapToResponse);
        } else {
            // Operator vê apenas seu próprio conteúdo
            return contentRepository.findByCompanyIdAndCreatedByWithFilters(
                    companyId.toString(), 
                    userEmail, 
                    productId != null ? productId.toString() : null, 
                    published, 
                    channel, 
                    pageable)
                    .map(this::mapToResponse);
        }
    }

    @Transactional(readOnly = true)
    public ContentResponse getContentById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso
        validateContentAccess(content, userEmail);
        
        return mapToResponse(content);
    }

    public ContentResponse updateContent(UUID id, ContentRequest request) {
        log.info("Updating content: {}", id);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso para edição
        validateContentEditAccess(content, userEmail);
        
        // Validar se o produto existe
        if (request.getProductId() != null && !request.getProductId().equals(content.getProductId())) {
            productRepository.findByIdAndCompanyId(request.getProductId(), companyId)
                    .orElseThrow(() -> ResourceNotFoundException.product());
            content.setProductId(request.getProductId());
        }
        
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContent(request.getContent());
        content.setUpdatedAt(LocalDateTime.now());
        
        if (request.getChannels() != null) {
            content.setChannels(request.getChannels());
        }
        
        if (request.getScheduledDate() != null) {
            content.setScheduledDate(request.getScheduledDate());
        }
        
        Content savedContent = contentRepository.save(content);
        
        log.info("Content updated successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    public void deleteContent(UUID id) {
        log.info("Deleting content: {}", id);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso para exclusão
        validateContentEditAccess(content, userEmail);
        
        // Deletar métricas associadas
        contentMetricsRepository.deleteByContentId(id);
        
        contentRepository.delete(content);
        
        log.info("Content deleted successfully: {}", id);
    }

    public ContentResponse publishContent(UUID id, String[] channels) {
        log.info("Publishing content: {} to channels: {}", id, Arrays.toString(channels));
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso
        validateContentAccess(content, userEmail);
        
        // Adicionar canais sem duplicar
        List<String> currentChannels = content.getChannels();
        if (currentChannels == null) {
            currentChannels = new ArrayList<>();
        }
        
        for (String channel : channels) {
            if (!currentChannels.contains(channel)) {
                currentChannels.add(channel);
            }
        }
        
        content.setChannels(currentChannels);
        content.setPublished(true);
        content.setPublishedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        
        Content savedContent = contentRepository.save(content);
        
        log.info("Content published successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    public ContentResponse unpublishContent(UUID id, String[] channels) {
        log.info("Unpublishing content: {} from channels: {}", id, Arrays.toString(channels));
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso
        validateContentAccess(content, userEmail);
        
        List<String> currentChannels = content.getChannels();
        if (currentChannels != null && channels != null) {
            // Remover canais específicos
            for (String channel : channels) {
                currentChannels.remove(channel);
            }
        } else if (channels == null) {
            // Se não especificou canais, remove de todos
            currentChannels = new ArrayList<>();
        }
        
        content.setChannels(currentChannels);
        
        // Se não há mais canais, marcar como não publicado
        if (currentChannels == null || currentChannels.isEmpty()) {
            content.setPublished(false);
            content.setPublishedAt(null);
        }
        
        content.setUpdatedAt(LocalDateTime.now());
        
        Content savedContent = contentRepository.save(content);
        
        log.info("Content unpublished successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse> getMyContent(UUID productId, Boolean published, String channel, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        return contentRepository.findByCompanyIdAndCreatedByWithFilters(
                companyId.toString(), 
                userEmail, 
                productId != null ? productId.toString() : null, 
                published, 
                channel, 
                pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse> getPublishedContent(UUID productId, String channel, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        return contentRepository.findByCompanyIdWithFilters(
                companyId.toString(), 
                productId != null ? productId.toString() : null, 
                true, 
                channel, 
                pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse> getScheduledContent(UUID productId, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        LocalDateTime now = LocalDateTime.now();
        
        return contentRepository.findByCompanyIdAndScheduledDateAfter(companyId, productId, now, pageable)
                .map(this::mapToResponse);
    }

    public ContentResponse scheduleContent(UUID id, String scheduledDate, String[] channels) {
        log.info("Scheduling content: {} for date: {} on channels: {}", id, scheduledDate, Arrays.toString(channels));
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.content());
        
        // Validar acesso
        validateContentAccess(content, userEmail);
        
        // Parse da data
        LocalDateTime scheduledDateTime;
        try {
            scheduledDateTime = LocalDateTime.parse(scheduledDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            throw new BusinessException("Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss");
        }
        
        // Validar se a data é futura
        if (scheduledDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Scheduled date must be in the future");
        }
        
        content.setScheduledDate(scheduledDateTime);
        content.setChannels(Arrays.asList(channels));
        content.setUpdatedAt(LocalDateTime.now());
        
        Content savedContent = contentRepository.save(content);
        
        log.info("Content scheduled successfully: {}", savedContent.getId());
        return mapToResponse(savedContent);
    }

    private void validateDraftAccess(Draft draft, String userEmail) {
        if (!isAdmin() && !draft.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("Access denied to this draft");
        }
    }

    private void validateContentAccess(Content content, String userEmail) {
        if (!isAdmin() && !content.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("Access denied to this content");
        }
    }

    private void validateContentEditAccess(Content content, String userEmail) {
        if (!isAdmin() && !content.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("You can only edit your own content");
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));
    }

    private void createInitialMetrics(Content content) {
        ContentMetrics metrics = new ContentMetrics();
        metrics.setId(UUID.randomUUID());
        metrics.setContentId(content.getId());
        metrics.setCompanyId(content.getCompanyId());
        metrics.setViews(0L);
        metrics.setLikes(0L);
        metrics.setShares(0L);
        metrics.setComments(0L);
        metrics.setEngagementRate(0.0);
        metrics.setReach(0L);
        metrics.setImpressions(0L);
        metrics.setClickThroughRate(0.0);
        metrics.setConversionRate(0.0);
        metrics.setMetricsData(new HashMap<>());
        metrics.setCreatedAt(LocalDateTime.now());
        metrics.setUpdatedAt(LocalDateTime.now());
        
        contentMetricsRepository.save(metrics);
    }

    private ContentResponse mapToResponse(Content content) {
        ContentResponse response = new ContentResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setDescription(content.getDescription());
        response.setContent(content.getContent());
        response.setProductId(content.getProductId());
        response.setCompanyId(content.getCompanyId());
        response.setCreatedBy(content.getCreatedBy());
        response.setCreatedAt(content.getCreatedAt());
        response.setUpdatedAt(content.getUpdatedAt());
        response.setPublished(content.getPublished());
        response.setPublishedAt(content.getPublishedAt());
        response.setChannels(content.getChannels());
        response.setScheduledDate(content.getScheduledDate());
        return response;
    }
}
