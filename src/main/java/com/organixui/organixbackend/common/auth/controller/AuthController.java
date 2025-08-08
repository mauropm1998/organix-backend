package com.organixui.organixbackend.common.auth.controller;

import com.organixui.organixbackend.common.auth.dto.JwtResponse;
import com.organixui.organixbackend.common.auth.dto.LoginRequest;
import com.organixui.organixbackend.common.auth.dto.SignupRequest;
import com.organixui.organixbackend.common.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação.
 * Inclui operações de login, registro, logout e refresh token.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<JwtResponse> login(
            @Parameter(description = "Credenciais de login") @Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/signup")
    @Operation(summary = "Registro", description = "Registra uma nova empresa e usuário administrador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    public ResponseEntity<JwtResponse> signup(
            @Parameter(description = "Dados para registro") @Valid @RequestBody SignupRequest request) {        
        JwtResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Realiza logout invalidando o token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    })
    public ResponseEntity<Void> logout() {
        // Para JWT stateless, o logout é tratado no frontend removendo o token
        // Aqui podemos implementar blacklist de tokens se necessário
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Renova o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    public ResponseEntity<JwtResponse> refresh() {
        JwtResponse response = authService.refreshToken();
        return ResponseEntity.ok(response);
    }
}
