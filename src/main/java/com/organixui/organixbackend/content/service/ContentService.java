package com.organixui.organixbackend.content.service;

import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.content.dto.ContentRequest;
import com.organixui.organixbackend.content.dto.ContentResponse;
import com.organixui.organixbackend.content.dto.UpdateContentRequest;
import com.organixui.organixbackend.content.dto.TransformDraftRequest;
import com.organixui.organixbackend.content.dto.UpdateContentStatusRequest;
import com.organixui.organixbackend.content.dto.ChannelResponse;
import com.organixui.organixbackend.content.model.Content;
import com.organixui.organixbackend.content.model.ContentStatus;
import com.organixui.organixbackend.content.model.Channel;
import com.organixui.organixbackend.content.repository.ContentRepository;
import com.organixui.organixbackend.content.repository.ChannelRepository;
import com.organixui.organixbackend.performance.dto.ContentMetricsResponse;
import com.organixui.organixbackend.performance.dto.ChannelMetricResponse;
import com.organixui.organixbackend.performance.dto.UpdateContentMetricsRequest;
import com.organixui.organixbackend.performance.dto.UpdateChannelMetricRequest;
import com.organixui.organixbackend.performance.dto.UpdateContentChannelMetricsRequest;
import com.organixui.organixbackend.performance.model.ContentMetrics;
import com.organixui.organixbackend.performance.model.ChannelMetricData;
import com.organixui.organixbackend.performance.repository.ContentMetricsRepository;
import com.organixui.organixbackend.draft.model.Draft;
import com.organixui.organixbackend.draft.model.DraftStatus;
import com.organixui.organixbackend.draft.repository.DraftRepository;
import com.organixui.organixbackend.user.model.User;
import com.organixui.organixbackend.user.model.AdminType;
import com.organixui.organixbackend.user.repository.UserRepository;
import com.organixui.organixbackend.product.repository.ProductRepository;
import com.organixui.organixbackend.content.history.model.ContentStatusHistory;
import com.organixui.organixbackend.content.history.repository.ContentStatusHistoryRepository;
import com.organixui.organixbackend.content.history.model.ContentAuditLog;
import com.organixui.organixbackend.content.history.repository.ContentAuditLogRepository;
import com.organixui.organixbackend.content.history.dto.ContentStatusHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DraftRepository draftRepository;
    private final ContentMetricsRepository contentMetricsRepository;
    private final ContentStatusHistoryRepository contentStatusHistoryRepository;
    private final ContentAuditLogRepository contentAuditLogRepository;

    public List<ContentResponse> getAllContent() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        List<Content> contentList = contentRepository.findByCompanyIdOrderByCreationDateDesc(companyId);
        return contentList.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ContentResponse> getAllContent(ContentStatus status, UUID channelId, UUID productId, UUID userId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
    List<Content> contentList = contentRepository.searchContent(companyId, status, channelId, productId, userId);
    return contentList.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public com.organixui.organixbackend.content.dto.ContentStatsResponse getContentStats() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        long total = contentRepository.countByCompanyId(companyId);
        long inProduction = contentRepository.countByCompanyIdAndStatus(companyId, ContentStatus.IN_PRODUCTION);
        return com.organixui.organixbackend.content.dto.ContentStatsResponse.builder()
                .total(total)
                .inProduction(inProduction)
                .build();
    }

    public Page<ContentResponse> getAllContent(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Page<Content> contentPage = contentRepository.findByCompanyId(companyId, pageable);
        return contentPage.map(this::convertToResponse);
    }

    public Page<ContentResponse> getAllContent(ContentStatus status, UUID channelId, UUID productId, UUID userId, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Page<Content> page = contentRepository.searchContentPage(companyId, status, channelId, productId, userId, pageable);
        return page.map(this::convertToResponse);
    }

    public List<ContentResponse> getRecentContent(int days) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        java.time.LocalDateTime from = java.time.LocalDateTime.now().minusDays(days);
        List<Content> list = contentRepository.findByCompanyIdAndCreationDateGreaterThanEqualOrderByCreationDateDesc(companyId, from);
        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ContentResponse> getMyContent() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Content> contentList;
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            contentList = contentRepository.findByCompanyIdOrderByCreationDateDesc(companyId);
        } else {
            contentList = contentRepository.findByCreatorIdOrProducerIdOrderByCreationDateDesc(currentUserId, currentUserId);
        }
        
        return contentList.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public Page<ContentResponse> getMyContent(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page<Content> contentPage;
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            contentPage = contentRepository.findByCompanyId(companyId, pageable);
        } else {
            contentPage = contentRepository.findByCreatorIdOrProducerId(currentUserId, currentUserId, pageable);
        }
        
        return contentPage.map(this::convertToResponse);
    }

    public ContentResponse getContentById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        
        return convertToResponse(content);
    }

    @Transactional
    public ContentResponse createContent(ContentRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        if (request.getProductId() != null) {
            boolean productExists = productRepository.existsByIdAndCompanyId(request.getProductId(), companyId);
            if (!productExists) {
                throw new BusinessException("Product not found or doesn't belong to your company");
            }
        }
        
        List<Channel> channels = null;
        if (request.getChannelIds() != null && !request.getChannelIds().isEmpty()) {
            channels = channelRepository.findByIdIn(request.getChannelIds());
            if (channels.size() != request.getChannelIds().size()) {
                throw new BusinessException("One or more channels not found");
            }
        }
        
        // Valida o produtor se fornecido
        if (request.getProducerId() != null) {
            userRepository.findByIdAndCompanyId(request.getProducerId(), companyId)
                    .orElseThrow(() -> new BusinessException("Producer not found or doesn't belong to your company"));
        }
        
        Content content = new Content();
        content.setName(request.getName());
        content.setType(request.getType());
    content.setContent(request.getContent());
        content.setProductId(request.getProductId());
        content.setCreatorId(currentUserId);
        content.setProducerId(request.getProducerId());
        content.setChannels(channels);
        content.setCompanyId(companyId);
        
        // Define o status: usa o fornecido ou PENDING como padrão
        ContentStatus initialStatus = request.getStatus() != null ? request.getStatus() : ContentStatus.PENDING;
        content.setStatus(initialStatus);
        
        if (request.getPostDate() != null) {
            content.setPostDate(request.getPostDate());
        }
        if (request.getProductionStartDate() != null) {
            content.setProductionStartDate(request.getProductionStartDate());
        }
        if (request.getProductionEndDate() != null) {
            content.setProductionEndDate(request.getProductionEndDate());
        }
        if (request.getMetaAdsId() != null) {
            content.setMetaAdsId(request.getMetaAdsId());
        }
        if (request.getMetaAdsId() != null) {
            content.setMetaAdsId(request.getMetaAdsId());
        }
        
        Content savedContent = contentRepository.save(content);
    // Cria histórico inicial
    createStatusHistory(savedContent, currentUserId, null, savedContent.getStatus());
        return convertToResponse(savedContent);
    }

    @Transactional
    public ContentResponse updateContent(UUID id, UpdateContentRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        
        // ADMIN pode atualizar qualquer, OPERATOR apenas se for creator ou producer
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (currentUser.getAdminType() != AdminType.ADMIN) {
            if (!content.getCreatorId().equals(currentUserId) &&
                (content.getProducerId() == null || !content.getProducerId().equals(currentUserId))) {
                throw new BusinessException("You can only update your own content");
            }
        }
        
        if (request.getProductId() != null) {
            boolean productExists = productRepository.existsByIdAndCompanyId(request.getProductId(), companyId);
            if (!productExists) {
                throw new BusinessException("Product not found or doesn't belong to your company");
            }
        }
        
        if (request.getChannelIds() != null) {
            List<Channel> channels = channelRepository.findByIdIn(request.getChannelIds());
            if (channels.size() != request.getChannelIds().size()) {
                throw new BusinessException("One or more channels not found");
            }
            content.setChannels(channels);
        }
        
        // Valida o produtor se fornecido
        if (request.getProducerId() != null) {
            userRepository.findByIdAndCompanyId(request.getProducerId(), companyId)
                    .orElseThrow(() -> new BusinessException("Producer not found or doesn't belong to your company"));
        }
        
        if (request.getName() != null && !request.getName().equals(content.getName())) {
            audit(content, currentUserId, "name", content.getName(), request.getName());
            content.setName(request.getName());
        }
        
        if (request.getType() != null && !request.getType().equals(content.getType())) {
            audit(content, currentUserId, "type", content.getType(), request.getType());
            content.setType(request.getType());
        }
        
        if (request.getContent() != null && !request.getContent().equals(content.getContent())) {
            audit(content, currentUserId, "content", truncate(content.getContent()), truncate(request.getContent()));
            content.setContent(request.getContent());
        }

        if (request.getProductId() != null && (content.getProductId() == null || !request.getProductId().equals(content.getProductId()))) {
            audit(content, currentUserId, "productId", String.valueOf(content.getProductId()), String.valueOf(request.getProductId()));
            content.setProductId(request.getProductId());
        }
        
        if (request.getProducerId() != null && (content.getProducerId() == null || !request.getProducerId().equals(content.getProducerId()))) {
            audit(content, currentUserId, "producerId", String.valueOf(content.getProducerId()), String.valueOf(request.getProducerId()));
            content.setProducerId(request.getProducerId());
        }
        
        ContentStatus previousStatus = content.getStatus();
        if (request.getStatus() != null && request.getStatus() != content.getStatus()) {
            audit(content, currentUserId, "status", String.valueOf(content.getStatus()), String.valueOf(request.getStatus()));
            content.setStatus(request.getStatus());
        }
        
        if (request.getPostDate() != null && !request.getPostDate().equals(content.getPostDate())) {
            audit(content, currentUserId, "postDate", String.valueOf(content.getPostDate()), String.valueOf(request.getPostDate()));
            content.setPostDate(request.getPostDate());
        }
        if (request.getProductionStartDate() != null && !request.getProductionStartDate().equals(content.getProductionStartDate())) {
            audit(content, currentUserId, "productionStartDate", String.valueOf(content.getProductionStartDate()), String.valueOf(request.getProductionStartDate()));
            content.setProductionStartDate(request.getProductionStartDate());
        }
        if (request.getProductionEndDate() != null && !request.getProductionEndDate().equals(content.getProductionEndDate())) {
            audit(content, currentUserId, "productionEndDate", String.valueOf(content.getProductionEndDate()), String.valueOf(request.getProductionEndDate()));
            content.setProductionEndDate(request.getProductionEndDate());
        }
        if (request.getMetaAdsId() != null && !request.getMetaAdsId().equals(content.getMetaAdsId())) {
            audit(content, currentUserId, "metaAdsId", content.getMetaAdsId(), request.getMetaAdsId());
            content.setMetaAdsId(request.getMetaAdsId());
        }
        
        Content savedContent = contentRepository.save(content);
        if (request.getStatus() != null && previousStatus != savedContent.getStatus()) {
            createStatusHistory(savedContent, currentUserId, previousStatus, savedContent.getStatus());
        }
        return convertToResponse(savedContent);
    }

    @Transactional
    public ContentResponse assignProducer(UUID contentId, UUID producerId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User currentUser = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.getAdminType() != AdminType.ADMIN) {
            throw new BusinessException("Only admins can assign producers");
        }
        
        Content content = contentRepository.findByIdAndCompanyId(contentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        
        // Verifica se o produtor existe na empresa
        userRepository.findByIdAndCompanyId(producerId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Producer not found"));
        
        content.setProducerId(producerId);
        
    Content savedContent = contentRepository.save(content);
    createStatusHistory(savedContent, SecurityUtils.getCurrentUserId(), null, savedContent.getStatus());
        return convertToResponse(savedContent);
    }

    @Transactional
    public ContentResponse changeStatus(UUID id, ContentStatus newStatus) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        
    // Usa as regras de validação existentes (admin pode tudo; operador somente se for produtor e transição válida)
    validateStatusChange(content, newStatus);
        
        content.setStatus(newStatus);
        
        if (newStatus == ContentStatus.POSTED && content.getPostDate() == null) {
            content.setPostDate(LocalDateTime.now());
        }
    // Não altera automaticamente datas de produção: ficam como foram informadas (regras futuras podem preencher aqui)
        
        Content savedContent = contentRepository.save(content);
        return convertToResponse(savedContent);
    }

    @Transactional
    public void deleteContent(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        Content content = contentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.getAdminType() != AdminType.ADMIN &&
            !content.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("You can only delete content you created");
        }
        
        contentRepository.delete(content);
    }

    private void validateStatusChange(Content content, ContentStatus newStatus) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.getAdminType() == AdminType.ADMIN) {
            return;
        }
        
        if (content.getProducerId() == null || !content.getProducerId().equals(currentUserId)) {
            throw new BusinessException("Only assigned producers can change content status");
        }
        
        switch (content.getStatus()) {
            case PENDING:
                if (newStatus != ContentStatus.IN_PRODUCTION && newStatus != ContentStatus.CANCELED) {
                    throw new BusinessException("From PENDING, can only change to IN_PRODUCTION or CANCELED");
                }
                break;
            case IN_PRODUCTION:
                if (newStatus != ContentStatus.POSTED && newStatus != ContentStatus.CANCELED) {
                    throw new BusinessException("From IN_PRODUCTION, can only change to POSTED or CANCELED");
                }
                break;
            case POSTED:
                if (newStatus != ContentStatus.FINISHED) {
                    throw new BusinessException("From POSTED, can only change to FINISHED");
                }
                break;
            default:
                throw new BusinessException("Invalid status transition for " + content.getStatus());
        }
    }

    /**
     * Transforma um rascunho aprovado em conteúdo.
     */
    @Transactional
    public ContentResponse transformDraftToContent(UUID draftId, TransformDraftRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        // Busca o rascunho
        Draft draft = draftRepository.findByIdAndCompanyId(draftId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        
        // Verifica se o rascunho está aprovado
        if (draft.getStatus() != DraftStatus.APPROVED) {
            throw new BusinessException("Only approved drafts can be transformed into content");
        }
        
        // Verifica se o usuário pode transformar este rascunho
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.getAdminType() != AdminType.ADMIN && !draft.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("You can only transform your own drafts");
        }
        
        // Busca os canais
        List<Channel> channels = channelRepository.findAllById(request.getChannelIds());
        if (channels.size() != request.getChannelIds().size()) {
            throw new BusinessException("One or more channels not found");
        }
        
        // Valida o produto se fornecido
        if (request.getProductId() != null) {
            boolean productExists = productRepository.existsByIdAndCompanyId(request.getProductId(), companyId);
            if (!productExists) {
                throw new BusinessException("Product not found or doesn't belong to your company");
            }
        }
        
        // Valida o produtor se fornecido
        if (request.getProducerId() != null) {
            userRepository.findByIdAndCompanyId(request.getProducerId(), companyId)
                    .orElseThrow(() -> new BusinessException("Producer not found or doesn't belong to your company"));
        }
        
        // Cria o conteúdo a partir do rascunho
        Content content = new Content();
        content.setName(draft.getName());
        content.setType(draft.getType());
    content.setContent(draft.getContent());
        content.setProductId(request.getProductId());
        content.setCreatorId(draft.getCreatorId());
        content.setProducerId(request.getProducerId());
        content.setCompanyId(companyId);
        content.setStatus(request.getStatus());
        content.setPostDate(request.getPostDate());
        content.setChannels(channels);
        if (request.getProductionStartDate() != null) {
            content.setProductionStartDate(request.getProductionStartDate());
        }
        if (request.getProductionEndDate() != null) {
            content.setProductionEndDate(request.getProductionEndDate());
        }
        
        content = contentRepository.save(content);
        // histórico inicial
    createStatusHistory(content, currentUserId, null, content.getStatus());
        
        // Remove o rascunho original
        draftRepository.delete(draft);
        
        return convertToResponse(content);
    }

    private ContentResponse convertToResponse(Content content) {
        User creator = userRepository.findById(content.getCreatorId()).orElse(null);
        
        User producer = null;
        if (content.getProducerId() != null) {
            producer = userRepository.findById(content.getProducerId()).orElse(null);
        }
        
        List<ChannelResponse> channelResponses = content.getChannels() != null ? 
                content.getChannels().stream()
                        .map(this::convertChannelToResponse)
                        .collect(Collectors.toList()) : null;
        
        // Busca métricas do conteúdo
        ContentMetricsResponse metricsResponse = null;
        Optional<ContentMetrics> metricsOpt = contentMetricsRepository.findByContentId(content.getId());
        if (metricsOpt.isPresent()) {
            metricsResponse = convertMetricsToResponse(metricsOpt.get());
        }
        
    // Histórico de status
    List<ContentStatusHistoryResponse> historyResponses = contentStatusHistoryRepository
        .findByContentIdOrderByChangedAtAsc(content.getId())
        .stream()
        .map(h -> ContentStatusHistoryResponse.builder()
            .id(h.getId())
            .contentId(content.getId())
            .userId(h.getUserId())
            .userName(userRepository.findById(h.getUserId()).map(User::getName).orElse(null))
            .newStatus(h.getNewStatus())
            .previousStatus(h.getPreviousStatus())
            .changedAt(h.getChangedAt())
            .build())
        .collect(Collectors.toList());

    return ContentResponse.builder()
                .id(content.getId())
                .name(content.getName())
                .type(content.getType())
                .content(content.getContent())
                .productId(content.getProductId())
                .creatorId(content.getCreatorId())
                .creatorName(creator != null ? creator.getName() : null)
                .creationDate(content.getCreationDate())
                .postDate(content.getPostDate())
                .productionStartDate(content.getProductionStartDate())
                .productionEndDate(content.getProductionEndDate())
                .metaAdsId(content.getMetaAdsId())
                .producerId(content.getProducerId())
                .producerName(producer != null ? producer.getName() : null)
                .status(content.getStatus())
                .channels(channelResponses)
                .companyId(content.getCompanyId())
                .metrics(metricsResponse)
                .history(historyResponses)
                .build();
    }
    
    @Transactional
    public ContentResponse updateContentStatus(UUID contentId, UpdateContentStatusRequest request) {
        // Busca o conteúdo
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado"));
        
        // ADMIN (mesma empresa) ou OPERATOR (se creator/producer) podem alterar
        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getAdminType().equals(AdminType.ADMIN)) {
            if (!content.getCompanyId().equals(currentUser.getCompanyId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        } else {
            if (!content.getCreatorId().equals(currentUser.getId()) &&
                !content.getProducerId().equals(currentUser.getId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        }
        
        // Captura status anterior e atualiza
        ContentStatus previousStatus = content.getStatus();
        ContentStatus newStatus = request.getStatus();
        if (newStatus == null) {
            throw new BusinessException("Novo status não pode ser nulo");
        }
        if (previousStatus == newStatus) {
            // Nada a fazer além de retornar
            return convertToResponse(content);
        }
        content.setStatus(newStatus);
        content = contentRepository.save(content);
        // Cria histórico
        createStatusHistory(content, currentUser.getId(), previousStatus, newStatus);
        
        log.info("Status do conteúdo {} atualizado para {} pelo usuário {}", 
                contentId, request.getStatus(), currentUser.getEmail());
        
        return convertToResponse(content);
    }
    
    private ChannelResponse convertChannelToResponse(Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .createdAt(channel.getCreatedAt())
                .build();
    }
    
    private ContentMetricsResponse convertMetricsToResponse(ContentMetrics metrics) {
        List<ChannelMetricResponse> channelMetrics = metrics.getChannelMetrics() != null ?
                metrics.getChannelMetrics().stream()
                        .map(this::convertChannelMetricToResponse)
                        .collect(Collectors.toList()) : new ArrayList<>();
        
        return ContentMetricsResponse.builder()
                .id(metrics.getId())
                .contentId(metrics.getContentId())
                .views(metrics.getViews())
                .likes(metrics.getLikes())
                .reach(metrics.getReach())
                .engagement(metrics.getEngagement())
                .comments(metrics.getComments())
                .shares(metrics.getShares())
                .channelMetrics(channelMetrics)
                .build();
    }

    private void createStatusHistory(Content content, UUID userId, ContentStatus previousStatus, ContentStatus newStatus) {
        try {
            ContentStatusHistory history = ContentStatusHistory.builder()
                    .content(content)
                    .userId(userId)
                    .newStatus(newStatus)
                    .previousStatus(previousStatus)
                    .companyId(content.getCompanyId())
                    .build();
            contentStatusHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("Failed to persist content status history for content {}: {}", content.getId(), e.getMessage());
        }
    }

    private void audit(Content content, UUID userId, String field, String oldVal, String newVal) {
        try {
            ContentAuditLog logEntry = ContentAuditLog.builder()
                    .contentId(content.getId())
                    .fieldName(field)
                    .oldValue(oldVal)
                    .newValue(newVal)
                    .userId(userId)
                    .companyId(content.getCompanyId())
                    .build();
            contentAuditLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to persist audit log for content {} field {}: {}", content.getId(), field, e.getMessage());
        }
    }

    private String truncate(String value) {
        if (value == null) return null;
        return value.length() > 500 ? value.substring(0, 497) + "..." : value;
    }
    
    private ChannelMetricResponse convertChannelMetricToResponse(ChannelMetricData channelData) {
        return ChannelMetricResponse.builder()
                .id(channelData.getId())
                .channelId(channelData.getChannelId())
                .channelName(channelData.getChannelName())
                .likes(channelData.getLikes())
                .comments(channelData.getComments())
                .shares(channelData.getShares())
                .siteVisits(channelData.getSiteVisits())
                .newAccounts(channelData.getNewAccounts())
                .postClicks(channelData.getPostClicks())
                .build();
    }
    
    @Transactional
    public ContentMetricsResponse updateContentMetrics(UUID contentId, UpdateContentMetricsRequest request) {
        // Verifica se o conteúdo existe
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado"));
        
        // ADMIN (mesma empresa) ou OPERATOR (se creator/producer) podem atualizar
        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getAdminType().equals(AdminType.ADMIN)) {
            if (!content.getCompanyId().equals(currentUser.getCompanyId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        } else {
            if (!content.getCreatorId().equals(currentUser.getId()) &&
                !content.getProducerId().equals(currentUser.getId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        }
        
        // Busca ou cria as métricas
        Optional<ContentMetrics> metricsOpt = contentMetricsRepository.findByContentId(contentId);
        ContentMetrics metrics;
        if (metricsOpt.isPresent()) {
            metrics = metricsOpt.get();
        } else {
            metrics = new ContentMetrics();
            metrics.setContentId(contentId);
        }
        
        // Atualiza os valores
        metrics.setViews(request.getViews());
        metrics.setLikes(request.getLikes());
        metrics.setReach(request.getReach());
        metrics.setEngagement(request.getEngagement());
        metrics.setComments(request.getComments());
        metrics.setShares(request.getShares());
        
        metrics = contentMetricsRepository.save(metrics);
        
        log.info("Métricas do conteúdo {} atualizadas pelo usuário {}", 
                contentId, currentUser.getEmail());
        
        return convertMetricsToResponse(metrics);
    }
    
    @Transactional
    public ChannelMetricResponse updateChannelMetrics(UUID contentId, UUID channelId, UpdateChannelMetricRequest request) {
        // Verifica se o conteúdo existe
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado"));
        
        // ADMIN (mesma empresa) ou OPERATOR (se creator/producer) podem atualizar
        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getAdminType().equals(AdminType.ADMIN)) {
            if (!content.getCompanyId().equals(currentUser.getCompanyId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        } else {
            if (!content.getCreatorId().equals(currentUser.getId()) &&
                !content.getProducerId().equals(currentUser.getId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        }
        
        // Busca ou cria as métricas do conteúdo
        Optional<ContentMetrics> contentMetricsOpt = contentMetricsRepository.findByContentId(contentId);
        ContentMetrics contentMetrics;
        if (contentMetricsOpt.isPresent()) {
            contentMetrics = contentMetricsOpt.get();
        } else {
            contentMetrics = new ContentMetrics();
            contentMetrics.setContentId(contentId);
            contentMetrics = contentMetricsRepository.save(contentMetrics);
        }
        
        // Busca o canal para obter o nome
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Canal não encontrado"));
        
        // Busca ou cria as métricas do canal
        ChannelMetricData channelMetric = contentMetrics.getChannelMetrics() != null ? 
                contentMetrics.getChannelMetrics().stream()
                        .filter(cm -> cm.getChannelId().equals(channelId))
                        .findFirst()
                        .orElse(null) : null;
        
        if (channelMetric == null) {
            channelMetric = new ChannelMetricData();
            channelMetric.setChannelId(channelId);
            channelMetric.setChannelName(channel.getName());
            channelMetric.setContentMetrics(contentMetrics);
            
            if (contentMetrics.getChannelMetrics() == null) {
                contentMetrics.setChannelMetrics(new ArrayList<>());
            }
            contentMetrics.getChannelMetrics().add(channelMetric);
        }
        
        // Atualiza as métricas do canal
        channelMetric.setLikes(request.getLikes());
        channelMetric.setComments(request.getComments());
        channelMetric.setShares(request.getShares());
        channelMetric.setSiteVisits(request.getSiteVisits());
        channelMetric.setNewAccounts(request.getNewAccounts());
        channelMetric.setPostClicks(request.getPostClicks());
        
        // Recalcula os totais baseado nos dados de canal
        if (contentMetrics.getChannelMetrics() != null) {
            contentMetrics.setLikes(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getLikes).sum());
            contentMetrics.setComments(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getComments).sum());
            contentMetrics.setShares(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getShares).sum());
        }
        
        contentMetrics = contentMetricsRepository.save(contentMetrics);
        
        log.info("Métricas do canal {} para conteúdo {} atualizadas pelo usuário {}", 
                channelId, contentId, currentUser.getEmail());
        
        return convertChannelMetricToResponse(channelMetric);
    }
    
    @Transactional
    public ContentMetricsResponse updateContentChannelMetrics(UUID contentId, UpdateContentChannelMetricsRequest request) {
        // Verifica se o conteúdo existe
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado"));
        
        // ADMIN (mesma empresa) ou OPERATOR (se creator/producer) podem atualizar
        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getAdminType().equals(AdminType.ADMIN)) {
            if (!content.getCompanyId().equals(currentUser.getCompanyId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        } else {
            if (!content.getCreatorId().equals(currentUser.getId()) &&
                !content.getProducerId().equals(currentUser.getId())) {
                throw new BusinessException("Acesso negado a este conteúdo");
            }
        }
        
        // Busca ou cria as métricas do conteúdo
        Optional<ContentMetrics> contentMetricsOpt = contentMetricsRepository.findByContentId(contentId);
        ContentMetrics contentMetrics;
        if (contentMetricsOpt.isPresent()) {
            contentMetrics = contentMetricsOpt.get();
        } else {
            contentMetrics = new ContentMetrics();
            contentMetrics.setContentId(contentId);
            contentMetrics = contentMetricsRepository.save(contentMetrics);
        }
        
        // Atualiza as métricas de cada canal
        for (UpdateChannelMetricRequest channelRequest : request.getChannelMetrics()) {
            UUID channelId = channelRequest.getChannelId();
            
            // Busca o canal para obter o nome
            Channel channel = channelRepository.findById(channelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Canal " + channelId + " não encontrado"));
            
            // Busca ou cria as métricas do canal
            ChannelMetricData channelMetric = contentMetrics.getChannelMetrics() != null ? 
                    contentMetrics.getChannelMetrics().stream()
                            .filter(cm -> cm.getChannelId().equals(channelId))
                            .findFirst()
                            .orElse(null) : null;
            
            if (channelMetric == null) {
                channelMetric = new ChannelMetricData();
                channelMetric.setChannelId(channelId);
                channelMetric.setChannelName(channel.getName());
                channelMetric.setContentMetrics(contentMetrics);
                
                if (contentMetrics.getChannelMetrics() == null) {
                    contentMetrics.setChannelMetrics(new ArrayList<>());
                }
                contentMetrics.getChannelMetrics().add(channelMetric);
            }
            
            // Atualiza as métricas do canal
            channelMetric.setLikes(channelRequest.getLikes());
            channelMetric.setComments(channelRequest.getComments());
            channelMetric.setShares(channelRequest.getShares());
            channelMetric.setSiteVisits(channelRequest.getSiteVisits());
            channelMetric.setNewAccounts(channelRequest.getNewAccounts());
            channelMetric.setPostClicks(channelRequest.getPostClicks());
        }
        
        // Recalcula os totais baseado nos dados de todos os canais
        if (contentMetrics.getChannelMetrics() != null) {
            contentMetrics.setLikes(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getLikes).sum());
            contentMetrics.setComments(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getComments).sum());
            contentMetrics.setShares(contentMetrics.getChannelMetrics().stream()
                    .mapToInt(ChannelMetricData::getShares).sum());
            
            // Para views, reach e engagement, vamos usar uma lógica diferente já que não temos por canal
            // Por enquanto, vamos manter os valores existentes ou zero se não existirem
            if (contentMetrics.getViews() == null) contentMetrics.setViews(0);
            if (contentMetrics.getReach() == null) contentMetrics.setReach(0);
            if (contentMetrics.getEngagement() == null) contentMetrics.setEngagement(0);
        }
        
        contentMetrics = contentMetricsRepository.save(contentMetrics);
        
        log.info("Métricas de todos os canais para conteúdo {} atualizadas pelo usuário {}", 
                contentId, currentUser.getEmail());
        
        return convertMetricsToResponse(contentMetrics);
    }
}
