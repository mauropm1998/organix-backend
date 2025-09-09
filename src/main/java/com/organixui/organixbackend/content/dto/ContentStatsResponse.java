package com.organixui.organixbackend.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas agregadas de conteúdo da empresa")
public class ContentStatsResponse {

    @Schema(description = "Total de registros de conteúdo da empresa", example = "128")
    private long total;

    @Schema(description = "Total de conteúdos atualmente com status IN_PRODUCTION", example = "37")
    private long inProduction;
}
