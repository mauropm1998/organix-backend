package com.organixui.organixbackend.calendar.model;

/**
 * Enum que representa os tipos de eventos do calendário.
 */
public enum EventType {
    CONTENT_PLANNING("Content Planning"),
    CONTENT_CREATION("Content Creation"),
    CONTENT_REVIEW("Content Review"),
    CONTENT_APPROVAL("Content Approval"),
    CONTENT_PUBLICATION("Content Publication"),
    CAMPAIGN_LAUNCH("Campaign Launch"),
    MEETING("Meeting"),
    DEADLINE("Deadline"),
    REMINDER("Reminder"),
    OTHER("Other");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
