package com.scoutplay.ScoutPlay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS para permitir requisições do frontend React
 * Opera em conjunto com as configurações em application.properties
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
                
                // Adicione mais mapeamentos conforme necessário para outros paths
                registry.addMapping("/login")
                    .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000")
                    .allowedMethods("GET", "POST", "OPTIONS")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
