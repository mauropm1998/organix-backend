package com.organixui.organixbackend.content.dto;

import com.organixui.organixbackend.content.model.ContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para transformar um rascunho em conteúdo.
 */
@Data
@Schema(description = "Dados para transformar um rascunho em conteúdo")
public class TransformDraftRequest {

    @Schema(description = "Status inicial do conteúdo", example = "PENDING")
    private ContentStatus status = ContentStatus.PENDING;

    @Schema(description = "IDs dos canais onde o conteúdo será publicado", required = true)
    @NotEmpty(message = "Pelo menos um canal deve ser selecionado")
    private List<UUID> channelIds;

    @Schema(description = "Data de publicação planejada")
    private LocalDateTime postDate;

    @Schema(description = "ID do produto associado (opcional)")
    private UUID productId;

    @Schema(description = "ID do usuário produtor (opcional, se diferente do criador)")
    private UUID producerId;
}