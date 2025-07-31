package com.organixui.organixbackend.calendar.dto;

import com.organixui.organixbackend.calendar.model.EventStatus;
import com.organixui.organixbackend.calendar.model.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Response DTO para eventos do calendário")
public class CalendarEventResponse {

    @Schema(description = "ID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Título do evento", example = "Publicação do conteúdo X")
    private String title;

    @Schema(description = "Descrição do evento", example = "Publicar conteúdo X nas redes sociais")
    private String description;

    @Schema(description = "Tipo do evento", example = "CONTENT_PUBLICATION")
    private EventType eventType;

    @Schema(description = "Data e hora de início", example = "2024-01-15T10:30:00")
    private LocalDateTime startDate;

    @Schema(description = "Data e hora de fim", example = "2024-01-15T11:30:00")
    private LocalDateTime endDate;

    @Schema(description = "ID do conteúdo relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID contentId;

    @Schema(description = "ID do draft relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID draftId;

    @Schema(description = "ID do produto relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;

    @Schema(description = "ID da empresa", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID companyId;

    @Schema(description = "Email do criador do evento", example = "user@example.com")
    private String createdBy;

    @Schema(description = "Lista de canais para o evento", example = "[\"instagram\", \"facebook\"]")
    private List<String> channels;

    @Schema(description = "Status do evento", example = "SCHEDULED")
    private EventStatus status;

    @Schema(description = "Indica se o evento é recorrente", example = "false")
    private Boolean recurring;

    @Schema(description = "Padrão de recorrência", example = "WEEKLY")
    private String recurrencePattern;

    @Schema(description = "Indica se o evento dura o dia todo", example = "false")
    private Boolean allDay;

    @Schema(description = "Data de criação do evento", example = "2024-01-10T08:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-10T08:00:00")
    private LocalDateTime updatedAt;
}
