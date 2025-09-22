package com.organixui.organixbackend.product.service;

import com.organixui.organixbackend.common.exception.ResourceNotFoundException;
import com.organixui.organixbackend.common.security.SecurityUtils;
import com.organixui.organixbackend.product.dto.CreateProductRequest;
import com.organixui.organixbackend.product.dto.ProductResponse;
import com.organixui.organixbackend.product.dto.UpdateProductRequest;
import com.organixui.organixbackend.product.model.Product;
import com.organixui.organixbackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de produtos.
 * Inclui operações CRUD com isolamento por empresa (multi-tenancy).
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Lista todos os produtos da empresa do usuário autenticado.
     */
    public List<ProductResponse> getAllProducts() {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        List<Product> products = productRepository.findByCompanyId(companyId);
        
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca produto por ID dentro da empresa do usuário autenticado.
     */
    public ProductResponse getProductById(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Product product = productRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.product(id.toString()));
        
        return convertToResponse(product);
    }
    
    /**
     * Cria um novo produto na empresa do usuário autenticado.
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        
        Product product = new Product();
        product.setName(request.getName());
        product.setCompanyId(companyId);
        
        product = productRepository.saveAndFlush(product);
        return convertToResponse(product);
    }
    
    /**
     * Atualiza um produto existente na empresa do usuário autenticado.
     */
    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Product product = productRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.product(id.toString()));
        
        product.setName(request.getName());
        
        product = productRepository.save(product);
        return convertToResponse(product);
    }
    
    /**
     * Exclui um produto da empresa do usuário autenticado.
     */
    @Transactional
    public void deleteProduct(UUID id) {
        UUID companyId = SecurityUtils.getCurrentUserCompanyId();
        Product product = productRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> ResourceNotFoundException.product(id.toString()));
        
        productRepository.delete(product);
    }
    
    /**
     * Converte uma entidade Product para DTO de resposta.
     */
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCompanyId()
        );
    }
}
