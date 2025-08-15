package com.organixui.organixbackend.config;

import com.organixui.organixbackend.content.model.Channel;
import com.organixui.organixbackend.content.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Runner para inicializar dados padrão na aplicação.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializationRunner implements CommandLineRunner {

    private final ChannelRepository channelRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultChannels();
    }

    /**
     * Inicializa os canais padrão globais.
     */
    private void initializeDefaultChannels() {
        log.info("Verificando e inicializando canais padrão globais...");

        List<String> defaultChannelNames = Arrays.asList(
                "Facebook",
                "LinkedIn", 
                "YouTube",
                "Instagram",
                "TikTok"
        );

        for (String channelName : defaultChannelNames) {
            // Verifica se o canal já existe
            if (!channelRepository.existsByName(channelName)) {
                Channel channel = new Channel();
                channel.setName(channelName);
                
                channelRepository.save(channel);
                log.info("Canal global criado: {}", channelName);
            } else {
                log.debug("Canal '{}' já existe, pulando criação", channelName);
            }
        }

        log.info("Inicialização de canais padrão concluída. Total de canais: {}", 
                channelRepository.count());
    }
}
