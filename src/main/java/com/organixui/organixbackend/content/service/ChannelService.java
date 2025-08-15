package com.organixui.organixbackend.content.service;

import com.organixui.organixbackend.content.dto.ChannelResponse;
import com.organixui.organixbackend.content.model.Channel;
import com.organixui.organixbackend.content.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações relacionadas aos canais.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;

    /**
     * Busca todos os canais disponíveis.
     * 
     * @return Lista de todos os canais como ChannelResponse
     */
    public List<ChannelResponse> getAllChannels() {
        log.debug("Buscando todos os canais disponíveis");
        List<Channel> channels = channelRepository.findAll();
        log.debug("Encontrados {} canais", channels.size());
        
        return channels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private ChannelResponse convertToResponse(Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .createdAt(channel.getCreatedAt())
                .build();
    }
}
