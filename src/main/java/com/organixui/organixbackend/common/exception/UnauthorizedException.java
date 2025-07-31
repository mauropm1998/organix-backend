package com.organixui.organixbackend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando o usuário não tem permissão para acessar um recurso.
 * Utilizada para controle de acesso baseado em roles e propriedade de recursos.
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS");
    }
    
    // Métodos utilitários para cenários comuns de acesso negado
    public static UnauthorizedException accessDenied() {
        return new UnauthorizedException("Access denied. Insufficient permissions.");
    }
    
    public static UnauthorizedException companyMismatch() {
        return new UnauthorizedException("Access denied. Resource belongs to different company.");
    }
    
    public static UnauthorizedException resourceOwnership(String resource) {
        return new UnauthorizedException("Access denied. You don't have permission to access this " + resource + ".");
    }
}
