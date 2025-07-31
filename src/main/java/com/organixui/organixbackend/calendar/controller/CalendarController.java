package com.organixui.organixbackend.calendar.controller;

import com.organixui.organixbackend.calendar.dto.CalendarEventRequest;
import com.organixui.organixbackend.calendar.dto.CalendarEventResponse;
import com.organixui.organixbackend.calendar.model.EventStatus;
import com.organixui.organixbackend.calendar.model.EventType;
import com.organixui.organixbackend.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Calendar and event management endpoints")
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping("/events")
    @Operation(summary = "Create a new calendar event")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<CalendarEventResponse> createEvent(@Valid @RequestBody CalendarEventRequest request) {
        CalendarEventResponse event = calendarService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/events")
    @Operation(summary = "Get all calendar events with pagination and filtering")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<Page<CalendarEventResponse>> getAllEvents(
            @RequestParam(required = false) @Parameter(description = "Filter by event type") EventType eventType,
            @RequestParam(required = false) @Parameter(description = "Filter by event status") EventStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Filter by start date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @Parameter(description = "Filter by end date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<CalendarEventResponse> events = calendarService.getAllEvents(eventType, status, productId, startDate, endDate, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get calendar event by ID")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<CalendarEventResponse> getEventById(@PathVariable UUID id) {
        CalendarEventResponse event = calendarService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Update calendar event")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody CalendarEventRequest request) {
        CalendarEventResponse event = calendarService.updateEvent(id, request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Delete calendar event")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/range")
    @Operation(summary = "Get events in a specific date range")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<CalendarEventResponse>> getEventsInDateRange(
            @RequestParam @Parameter(description = "Start date", required = true) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @Parameter(description = "End date", required = true) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CalendarEventResponse> events = calendarService.getEventsInDateRange(startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/today")
    @Operation(summary = "Get today's events")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<CalendarEventResponse>> getTodaysEvents() {
        List<CalendarEventResponse> events = calendarService.getTodaysEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/upcoming")
    @Operation(summary = "Get upcoming events (next 7 days)")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<CalendarEventResponse>> getUpcomingEvents() {
        List<CalendarEventResponse> events = calendarService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/my")
    @Operation(summary = "Get events created by current user")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public ResponseEntity<Page<CalendarEventResponse>> getMyEvents(
            @RequestParam(required = false) @Parameter(description = "Filter by event type") EventType eventType,
            @RequestParam(required = false) @Parameter(description = "Filter by event status") EventStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by product ID") UUID productId,
            @RequestParam(required = false) @Parameter(description = "Filter by start date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @Parameter(description = "Filter by end date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<CalendarEventResponse> events = calendarService.getMyEvents(eventType, status, productId, startDate, endDate, pageable);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/events/{id}/status")
    @Operation(summary = "Update event status")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<CalendarEventResponse> updateEventStatus(
            @PathVariable UUID id,
            @RequestParam @Parameter(description = "New event status") EventStatus status) {
        CalendarEventResponse event = calendarService.updateEventStatus(id, status);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events/by-content/{contentId}")
    @Operation(summary = "Get events related to specific content")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<CalendarEventResponse>> getEventsByContent(@PathVariable UUID contentId) {
        List<CalendarEventResponse> events = calendarService.getEventsByContent(contentId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/by-draft/{draftId}")
    @Operation(summary = "Get events related to specific draft")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<CalendarEventResponse>> getEventsByDraft(@PathVariable UUID draftId) {
        List<CalendarEventResponse> events = calendarService.getEventsByDraft(draftId);
        return ResponseEntity.ok(events);
    }
}
