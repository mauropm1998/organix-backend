package com.organixui.organixbackend.calendar.service;

import com.organixui.organixbackend.calendar.dto.CalendarEventRequest;
import com.organixui.organixbackend.calendar.dto.CalendarEventResponse;
import com.organixui.organixbackend.calendar.model.CalendarEvent;
import com.organixui.organixbackend.calendar.model.EventStatus;
import com.organixui.organixbackend.calendar.model.EventType;
import com.organixui.organixbackend.calendar.repository.CalendarEventRepository;
import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;

    public CalendarEventResponse createEvent(CalendarEventRequest request) {
        log.info("Creating calendar event: {}", request.getTitle());
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        // Validar datas
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }
        
        CalendarEvent event = new CalendarEvent();
        event.setId(UUID.randomUUID());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setContentId(request.getContentId());
        event.setDraftId(request.getDraftId());
        event.setProductId(request.getProductId());
        event.setCompanyId(companyId);
        event.setCreatedBy(userEmail);
        event.setChannels(request.getChannels());
        event.setStatus(request.getStatus() != null ? request.getStatus() : EventStatus.SCHEDULED);
        event.setRecurring(request.getRecurring() != null ? request.getRecurring() : false);
        event.setRecurrencePattern(request.getRecurrencePattern());
        event.setAllDay(request.getAllDay() != null ? request.getAllDay() : false);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        
        log.info("Calendar event created successfully: {}", savedEvent.getId());
        return mapToResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public Page<CalendarEventResponse> getAllEvents(EventType eventType, EventStatus status, UUID productId, 
                                                   LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        return calendarEventRepository.findByCompanyIdWithFilters(companyId, eventType, status, productId, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public CalendarEventResponse getEventById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        CalendarEvent event = calendarEventRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.calendarEvent());
        
        // Validar acesso
        validateEventAccess(event, userEmail);
        
        return mapToResponse(event);
    }

    public CalendarEventResponse updateEvent(UUID id, CalendarEventRequest request) {
        log.info("Updating calendar event: {}", id);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        CalendarEvent event = calendarEventRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.calendarEvent());
        
        // Validar acesso para edição
        validateEventEditAccess(event, userEmail);
        
        // Validar datas
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }
        
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setContentId(request.getContentId());
        event.setDraftId(request.getDraftId());
        event.setProductId(request.getProductId());
        event.setChannels(request.getChannels());
        
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }
        
        event.setRecurring(request.getRecurring() != null ? request.getRecurring() : false);
        event.setRecurrencePattern(request.getRecurrencePattern());
        event.setAllDay(request.getAllDay() != null ? request.getAllDay() : false);
        event.setUpdatedAt(LocalDateTime.now());
        
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        
        log.info("Calendar event updated successfully: {}", savedEvent.getId());
        return mapToResponse(savedEvent);
    }

    public void deleteEvent(UUID id) {
        log.info("Deleting calendar event: {}", id);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        CalendarEvent event = calendarEventRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.calendarEvent());
        
        // Validar acesso para exclusão
        validateEventEditAccess(event, userEmail);
        
        calendarEventRepository.delete(event);
        
        log.info("Calendar event deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getEventsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        List<CalendarEvent> events = calendarEventRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getTodaysEvents() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        LocalDateTime today = LocalDateTime.now();
        
        List<CalendarEvent> events = calendarEventRepository.findTodaysEvents(companyId, today);
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getUpcomingEvents() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        List<CalendarEvent> events = calendarEventRepository.findUpcomingEvents(companyId, start, end);
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CalendarEventResponse> getMyEvents(EventType eventType, EventStatus status, UUID productId, 
                                                  LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        return calendarEventRepository.findByCompanyIdAndCreatedBy(companyId, userEmail, pageable)
                .map(this::mapToResponse);
    }

    public CalendarEventResponse updateEventStatus(UUID id, EventStatus status) {
        log.info("Updating event status: {} to {}", id, status);
        
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        
        CalendarEvent event = calendarEventRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.calendarEvent());
        
        // Validar acesso
        validateEventAccess(event, userEmail);
        
        event.setStatus(status);
        event.setUpdatedAt(LocalDateTime.now());
        
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        
        log.info("Event status updated successfully: {}", savedEvent.getId());
        return mapToResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getEventsByContent(UUID contentId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        List<CalendarEvent> events = calendarEventRepository.findByContentIdAndCompanyId(contentId, companyId);
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getEventsByDraft(UUID draftId) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        List<CalendarEvent> events = calendarEventRepository.findByDraftIdAndCompanyId(draftId, companyId);
        
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateEventAccess(CalendarEvent event, String userEmail) {
        if (!isAdmin() && !event.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("Access denied to this calendar event");
        }
    }

    private void validateEventEditAccess(CalendarEvent event, String userEmail) {
        if (!isAdmin() && !event.getCreatedBy().equals(userEmail)) {
            throw new BusinessException("You can only edit your own calendar events");
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));
    }

    private CalendarEventResponse mapToResponse(CalendarEvent event) {
        CalendarEventResponse response = new CalendarEventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setEventType(event.getEventType());
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());
        response.setContentId(event.getContentId());
        response.setDraftId(event.getDraftId());
        response.setProductId(event.getProductId());
        response.setCompanyId(event.getCompanyId());
        response.setCreatedBy(event.getCreatedBy());
        response.setChannels(event.getChannels());
        response.setStatus(event.getStatus());
        response.setRecurring(event.getRecurring());
        response.setRecurrencePattern(event.getRecurrencePattern());
        response.setAllDay(event.getAllDay());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        return response;
    }
}
