package com.organixui.organixbackend.content.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Request DTO para criação/atualização de conteúdo")
public class ContentRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Schema(description = "Título do conteúdo", example = "Novo lançamento do produto X")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Descrição do conteúdo", example = "Descrição detalhada do novo produto X")
    private String description;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters")
    @Schema(description = "Conteúdo completo", example = "Texto completo do conteúdo...")
    private String content;

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID do produto relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;

    @Schema(description = "Lista de canais onde o conteúdo será publicado", example = "[\"instagram\", \"facebook\", \"twitter\"]")
    private List<String> channels;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data agendada para publicação", example = "2024-01-15T10:30:00")
    private LocalDateTime scheduledDate;
}
