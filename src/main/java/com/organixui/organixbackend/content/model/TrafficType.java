package com.organixui.organixbackend.content.model;

/**
 * Enum para representar o tipo de tráfego do conteúdo.
 */
public enum TrafficType {
    PAID("Pago"),
    ORGANIC("Orgânico");
    
    private final String description;
    
    TrafficType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
