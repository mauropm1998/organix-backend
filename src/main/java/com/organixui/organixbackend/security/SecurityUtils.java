package com.organixui.organixbackend.security;

import com.organixui.organixbackend.user.model.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Getter
public class SecurityUtils {
    
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }
    
    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
    
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
    
    public static UUID getCurrentUserCompanyId() {
        return getCurrentUser().getCompanyId();
    }
    
    public static boolean isAdmin() {
        User user = getCurrentUser();
        return user.getAdminType().name().equals("ADMIN");
    }
    
    public static boolean isOperator() {
        User user = getCurrentUser();
        return user.getAdminType().name().equals("OPERATOR");
    }
    
    public static boolean canAccessResource(UUID resourceUserId) {
        User currentUser = getCurrentUser();
        return isAdmin() || currentUser.getId().equals(resourceUserId);
    }
}
