package com.organixui.organixbackend.content.controller;

import com.organixui.organixbackend.content.dto.ChannelResponse;
import com.organixui.organixbackend.content.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações relacionadas aos canais.
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channels", description = "Channel management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ChannelController {
    
    private final ChannelService channelService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Listar todos os canais", description = "Retorna lista de todos os canais disponíveis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canais retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<ChannelResponse>> getAllChannels() {
        List<ChannelResponse> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }
}
