package com.organixui.organixbackend.performance.repository;

import com.organixui.organixbackend.performance.model.ChannelMetricData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de banco de dados da entidade ChannelMetricData.
 */
@Repository
public interface ChannelMetricDataRepository extends JpaRepository<ChannelMetricData, UUID> {
    
    /**
     * Busca dados de métricas por ID das métricas de conteúdo.
     */
    Optional<ChannelMetricData> findByContentMetricsId(UUID contentMetricsId);
}
