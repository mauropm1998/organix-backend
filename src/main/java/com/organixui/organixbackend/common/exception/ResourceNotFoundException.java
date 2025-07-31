package com.organixui.organixbackend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando um recurso não é encontrado no sistema.
 * Utilizada para entidades que não existem ou não pertencem ao usuário/empresa.
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
    
    // Métodos utilitários para facilitar a criação de exceções específicas
    public static ResourceNotFoundException user(String identifier) {
        return new ResourceNotFoundException("User not found: " + identifier);
    }
    
    public static ResourceNotFoundException user() {
        return new ResourceNotFoundException("User not found");
    }
    
    public static ResourceNotFoundException company(String identifier) {
        return new ResourceNotFoundException("Company not found: " + identifier);
    }
    
    public static ResourceNotFoundException company() {
        return new ResourceNotFoundException("Company not found");
    }
    
    public static ResourceNotFoundException product(String identifier) {
        return new ResourceNotFoundException("Product not found: " + identifier);
    }
    
    public static ResourceNotFoundException product() {
        return new ResourceNotFoundException("Product not found");
    }
    
    public static ResourceNotFoundException draft(String identifier) {
        return new ResourceNotFoundException("Draft not found: " + identifier);
    }
    
    public static ResourceNotFoundException draft() {
        return new ResourceNotFoundException("Draft not found");
    }
    
    public static ResourceNotFoundException content(String identifier) {
        return new ResourceNotFoundException("Content not found: " + identifier);
    }
    
    public static ResourceNotFoundException content() {
        return new ResourceNotFoundException("Content not found");
    }
    
    public static ResourceNotFoundException calendarEvent(String identifier) {
        return new ResourceNotFoundException("Calendar event not found: " + identifier);
    }
    
    public static ResourceNotFoundException calendarEvent() {
        return new ResourceNotFoundException("Calendar event not found");
    }
}
