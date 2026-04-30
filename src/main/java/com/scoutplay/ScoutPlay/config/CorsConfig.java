package com.scoutplay.ScoutPlay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Configuração de CORS para permitir requisições do frontend React
 * A configuração é lida do application.properties e aplicada aqui
 */
@Configuration
public class CorsConfig {

    @Value("${spring.web.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(split(allowedOrigins))
                    .allowedMethods(split(allowedMethods))
                    .allowedHeaders(split(allowedHeaders))
                    .allowCredentials(allowCredentials)
                    .maxAge(maxAge);
            }
        };
    }

    private String[] split(String value) {
        return Arrays.stream(value.split(","))
            .map(String::trim)
            .filter(item -> !item.isEmpty())
            .toArray(String[]::new);
    }
}

