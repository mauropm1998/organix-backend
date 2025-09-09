package com.organixui.organixbackend.draft.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas agregadas de rascunhos da empresa")
public class DraftStatsResponse {

    @Schema(description = "Total de rascunhos da empresa", example = "54")
    private long total;

    @Schema(description = "Total de rascunhos aprovados (status APPROVED)", example = "18")
    private long approved;
}
