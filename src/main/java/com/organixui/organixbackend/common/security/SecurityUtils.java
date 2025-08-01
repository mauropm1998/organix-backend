package com.organixui.organixbackend.common.security;

import com.organixui.organixbackend.user.model.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utilitários para acessar informações do usuário autenticado e realizar
 * verificações de segurança comuns em toda a aplicação.
 */
@Getter
public class SecurityUtils {
    
    /**
     * Obtém o usuário atualmente autenticado.
     * @return O usuário autenticado
     * @throws RuntimeException se não houver usuário autenticado
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }
    
    /**
     * Obtém o ID do usuário atualmente autenticado.
     */
    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    /**
     * Obtém o username do usuário atualmente autenticado.
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
    
    /**
     * Obtém o email do usuário atualmente autenticado.
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
    
    /**
     * Obtém o ID da empresa do usuário atualmente autenticado.
     */
    public static UUID getCurrentUserCompanyId() {
        return getCurrentUser().getCompanyId();
    }
    
    /**
     * Verifica se o usuário atual é um administrador.
     */
    public static boolean isAdmin() {
        User user = getCurrentUser();
        return user.getAdminType().name().equals("ADMIN");
    }
    
    /**
     * Verifica se o usuário atual é um operador.
     */
    public static boolean isOperator() {
        User user = getCurrentUser();
        return user.getAdminType().name().equals("OPERATOR");
    }
    
    /**
     * Verifica se o usuário atual pode acessar um recurso.
     * Admins podem acessar qualquer recurso, operadores só os próprios.
     * 
     * @param resourceUserId ID do usuário dono do recurso
     * @return true se pode acessar, false caso contrário
     */
    public static boolean canAccessResource(UUID resourceUserId) {
        User currentUser = getCurrentUser();
        return isAdmin() || currentUser.getId().equals(resourceUserId);
    }
}
