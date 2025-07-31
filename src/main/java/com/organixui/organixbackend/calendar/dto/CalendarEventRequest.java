package com.organixui.organixbackend.calendar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.organixui.organixbackend.calendar.model.EventStatus;
import com.organixui.organixbackend.calendar.model.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Request DTO para criação/atualização de eventos do calendário")
public class CalendarEventRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Schema(description = "Título do evento", example = "Publicação do conteúdo X")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Descrição do evento", example = "Publicar conteúdo X nas redes sociais")
    private String description;

    @NotNull(message = "Event type is required")
    @Schema(description = "Tipo do evento", example = "CONTENT_PUBLICATION")
    private EventType eventType;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora de início", example = "2024-01-15T10:30:00")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora de fim", example = "2024-01-15T11:30:00")
    private LocalDateTime endDate;

    @Schema(description = "ID do conteúdo relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID contentId;

    @Schema(description = "ID do draft relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID draftId;

    @Schema(description = "ID do produto relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;

    @Schema(description = "Lista de canais para o evento", example = "[\"instagram\", \"facebook\"]")
    private List<String> channels;

    @Schema(description = "Status do evento", example = "SCHEDULED")
    private EventStatus status = EventStatus.SCHEDULED;

    @Schema(description = "Indica se o evento é recorrente", example = "false")
    private Boolean recurring = false;

    @Schema(description = "Padrão de recorrência (se aplicável)", example = "WEEKLY")
    private String recurrencePattern;

    @Schema(description = "Indica se o evento dura o dia todo", example = "false")
    private Boolean allDay = false;
}
