package com.organixui.organixbackend.content.repository;

import com.organixui.organixbackend.content.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados da entidade Channel.
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    
    /**
     * Busca canais por IDs.
     */
    List<Channel> findByIdIn(List<UUID> ids);
    
    /**
     * Busca canal por nome.
     */
    Optional<Channel> findByName(String name);
    
    /**
     * Verifica se existe um canal com o nome específico.
     */
    boolean existsByName(String name);
}
