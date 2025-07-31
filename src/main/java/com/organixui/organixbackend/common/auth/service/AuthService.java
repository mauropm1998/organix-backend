package com.organixui.organixbackend.common.auth.service;

import com.organixui.organixbackend.common.auth.dto.JwtResponse;
import com.organixui.organixbackend.common.auth.dto.LoginRequest;
import com.organixui.organixbackend.common.auth.dto.SignupRequest;
import com.organixui.organixbackend.common.exception.BusinessException;
import com.organixui.organixbackend.common.security.JwtTokenProvider;
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
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Utilizador não encontrado"));
        
        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        
        return new JwtResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAdminType(),
                user.getCompanyId(),
                company.getName()
        );
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
        
        return new JwtResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAdminType(),
                user.getCompanyId(),
                company.getName()
        );
    }
}
