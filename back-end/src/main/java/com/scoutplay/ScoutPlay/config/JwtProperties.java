package com.scoutplay.ScoutPlay.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long expirationMs;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                "app.jwt.secret não configurado. Defina a propriedade ou a variável de ambiente JWT_SECRET.");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException(
                "app.jwt.secret muito curto (mínimo 32 caracteres para HS256).");
        }
    }
}
