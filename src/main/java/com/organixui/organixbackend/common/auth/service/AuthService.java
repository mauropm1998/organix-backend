package com.organixui.organixbackend.common.auth.service;

import com.organixui.organixbackend.common.auth.dto.*;
import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.security.JwtTokenProvider;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.company.model.Company;
import com.organixui.organixbackend.company.repository.CompanyRepository;
import com.organixui.organixbackend.user.model.AdminType;
import com.organixui.organixbackend.user.model.User;
import com.organixui.organixbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pela autenticação de usuários.
 * Inclui operações de login e registro de novas empresas.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Autentica um usuário e retorna o token JWT.
     */
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Utilizador não encontrado"));
        
        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        
        return buildJwtResponse(user, company, token, refreshToken);
    }
    
    /**
     * Registra uma nova empresa e seu administrador.
     */
    @Transactional
    public JwtResponse signup(SignupRequest request) {
        // Verifica se o email já está em uso
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Este email já está sendo utilizado!");
        }
        
        // Cria a empresa
        Company company = new Company();
        company.setName(request.getCompanyName());
        company = companyRepository.save(company);
        
        // Cria o usuário administrador
        User user = new User();
        user.setName(request.getAdminName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAdminType(AdminType.ADMIN);
        user.setCompanyId(company.getId());
        user = userRepository.save(user);
        
        // Atualiza a empresa com o ID do administrador
        company.setAdminId(user.getId());
        company = companyRepository.save(company);
        
        // Gera o token JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        return buildJwtResponse(user, company, token, refreshToken);
    }
    
    /**
     * Renova o token JWT usando o refresh token.
     */
    public JwtResponse refreshToken() {
        User currentUser = SecurityUtils.getCurrentUser();
        Company company = companyRepository.findById(currentUser.getCompanyId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        
        String token = jwtTokenProvider.generateTokenFromUsername(currentUser.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshTokenFromUsername(currentUser.getEmail());
        
        return buildJwtResponse(currentUser, company, token, refreshToken);
    }
    
    /**
     * Constrói a resposta JWT com o formato esperado pelo frontend.
     */
    private JwtResponse buildJwtResponse(User user, Company company, String token, String refreshToken) {
        UserInfoDto userInfo = new UserInfoDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAdminType(),
                user.getCompanyId()
        );
        
        CompanyInfoDto companyInfo = new CompanyInfoDto(
                company.getId(),
                company.getName(),
                company.getCreatedAt(),
                company.getAdminId()
        );
        
        return new JwtResponse(userInfo, companyInfo, token, refreshToken);
    }
}
