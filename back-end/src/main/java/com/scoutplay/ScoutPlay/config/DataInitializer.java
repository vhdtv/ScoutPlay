package com.scoutplay.ScoutPlay.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.scoutplay.ScoutPlay.services.TipoContaService;
import com.scoutplay.ScoutPlay.services.TipoDetalhePerfilService;
import com.scoutplay.ScoutPlay.services.TipoMidiaService;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(TipoContaService tipoContaService, TipoMidiaService tipoMidiaService, TipoDetalhePerfilService tipoDetalhePerfil) {
        return args -> {
            tipoContaService.injetarValores();
            tipoMidiaService.injetarValores();
            tipoDetalhePerfil.injetarValores();
        };
    }
}
