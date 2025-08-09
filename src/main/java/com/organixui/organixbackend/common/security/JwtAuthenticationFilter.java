package com.organixui.organixbackend.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.organixui.organixbackend.common.auth.service.CustomUserDetailsService;

import java.io.IOException;

/**
 * Filtro JWT que intercepta todas as requisições para verificar a presença e validade
 * do token de autenticação. Se válido, configura o contexto de segurança do Spring.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Pula o processamento JWT para rotas públicas
        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Não foi possível verificar o token JWT", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrai o token JWT do header Authorization da requisição.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * Verifica se o caminho da requisição é público (não requer autenticação).
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/api/auth/") ||
               requestURI.startsWith("/api/health/") ||
               requestURI.startsWith("/h2-console/") ||
               requestURI.startsWith("/swagger-ui/") ||
               requestURI.equals("/swagger-ui.html") ||
               requestURI.startsWith("/v3/api-docs/") ||
               requestURI.startsWith("/api-docs/") ||
               requestURI.startsWith("/swagger-resources/") ||
               requestURI.startsWith("/webjars/");
    }
}
