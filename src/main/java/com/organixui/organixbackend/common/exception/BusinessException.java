package com.organixui.organixbackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exceção base para regras de negócio da aplicação.
 * Todas as exceções customizadas devem herdar desta classe.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    
    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
