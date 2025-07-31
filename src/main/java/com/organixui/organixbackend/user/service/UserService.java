package com.organixui.organixbackend.user.service;

import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.security.SecurityUtils;
import com.organixui.organixbackend.company.model.Company;
import com.organixui.organixbackend.company.repository.CompanyRepository;
import com.organixui.organixbackend.user.dto.CreateUserRequest;
import com.organixui.organixbackend.user.dto.UpdateUserRequest;
import com.organixui.organixbackend.user.dto.UserResponse;
import com.organixui.organixbackend.user.model.User;
import com.organixui.organixbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de usuários.
 * Inclui operações CRUD com controle de acesso e validações de negócio.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Lista todos os usuários da empresa do usuário autenticado.
     */
    public List<UserResponse> getAllUsers() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        List<User> users = userRepository.findByCompanyId(companyId);
        
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca usuário por ID dentro da empresa do usuário autenticado.
     */
    public UserResponse getUserById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User user = userRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));
        
        return convertToResponse(user);
    }
    
    /**
     * Cria um novo usuário na empresa do usuário autenticado.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        // Verifica se o email já está em uso
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already taken!");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAdminType(request.getAdminType());
        user.setCompanyId(companyId);
        
        user = userRepository.save(user);
        return convertToResponse(user);
    }
    
    /**
     * Atualiza um usuário existente na empresa do usuário autenticado.
     */
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User user = userRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));
        
        // Verifica se o email já está em uso por outro usuário
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already taken!");
        }
        
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAdminType(request.getAdminType());
        
        // Só atualiza a senha se foi fornecida
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        return convertToResponse(user);
    }
    
    /**
     * Exclui um usuário da empresa do usuário autenticado.
     * Não permite excluir o administrador da empresa.
     */
    @Transactional
    public void deleteUser(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        User user = userRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));
        
        // Impede a exclusão do administrador da empresa
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> ResourceNotFoundException.company(companyId.toString()));
        
        if (user.getId().equals(company.getAdminId())) {
            throw new BusinessException("Cannot delete company admin user");
        }
        
        userRepository.delete(user);
    }
    
    /**
     * Converte uma entidade User para DTO de resposta.
     */
    private UserResponse convertToResponse(User user) {
        Company company = companyRepository.findById(user.getCompanyId())
                .orElse(null);
        
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAdminType(),
                user.getCompanyId(),
                company != null ? company.getName() : null
        );
    }
}
