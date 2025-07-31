package com.organixui.organixbackend.calendar.model;

/**
 * Enum que representa o status de eventos do calendário.
 */
public enum EventStatus {
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    POSTPONED("Postponed"),
    DRAFT("Draft");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
