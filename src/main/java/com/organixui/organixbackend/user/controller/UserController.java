package com.organixui.organixbackend.user.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organixui.organixbackend.user.dto.CreateUserRequest;
import com.organixui.organixbackend.user.dto.UpdateUserRequest;
import com.organixui.organixbackend.user.dto.UserResponse;
import com.organixui.organixbackend.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de usuários.
 * Inclui operações CRUD com controle de acesso baseado em roles.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operações de gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários da empresa com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem listar usuários")
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - usuários só podem ver seus próprios dados")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário na empresa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já em uso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem criar usuários")
    })
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "Dados do novo usuário") @Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já em uso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - usuários só podem atualizar seus próprios dados")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Parameter(description = "Novos dados do usuário") @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário da empresa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Não é possível excluir o administrador da empresa"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem excluir usuários")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
