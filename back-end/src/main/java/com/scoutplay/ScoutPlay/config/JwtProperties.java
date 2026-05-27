package com.scoutplay.ScoutPlay.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Carrega configurações de JWT do application.properties
 * Uso: @Autowired private JwtProperties jwtProperties;
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long expirationMs;
}
