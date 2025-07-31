package com.organixui.organixbackend.company.repository;

import com.organixui.organixbackend.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para operações de banco de dados relacionadas às empresas.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
