package com.organixui.organixbackend.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO para atualizar métricas específicas de canal")
public class ChannelMetricsRequest {

    @Min(value = 0, message = "Likes must be non-negative")
    @Schema(description = "Número de curtidas no canal", example = "150")
    private Integer likes;

    @Min(value = 0, message = "Comments must be non-negative")
    @Schema(description = "Número de comentários no canal", example = "45")
    private Integer comments;

    @Min(value = 0, message = "Shares must be non-negative")
    @Schema(description = "Número de compartilhamentos no canal", example = "25")
    private Integer shares;

    @Min(value = 0, message = "Site visits must be non-negative")
    @Schema(description = "Visitas ao site geradas pelo canal", example = "100")
    private Integer siteVisits;

    @Min(value = 0, message = "New accounts must be non-negative")
    @Schema(description = "Novas contas criadas através do canal", example = "15")
    private Integer newAccounts;

    @Min(value = 0, message = "Post clicks must be non-negative")
    @Schema(description = "Cliques no post", example = "75")
    private Integer postClicks;
}
