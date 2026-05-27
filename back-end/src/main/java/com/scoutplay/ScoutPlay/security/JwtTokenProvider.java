package com.scoutplay.ScoutPlay.security;

import com.scoutplay.ScoutPlay.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia geração e validação de JWT tokens
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Gera um novo JWT token
     * @param userId ID do usuário
     * @param userType Tipo de usuário (ATLETA, OLHEIRO, RESPONSAVEL)
     * @return Token JWT assinado
     */
    public String generateToken(String userId, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);
        claims.put("userId", userId);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extrai o userId do token
     */
    public String extractUserId(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extrai o userType do token
     */
    public String extractUserType(String token) {
        return (String) getClaimsFromToken(token).get("userType");
    }

    /**
     * Valida se o token é válido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error("Erro ao validar JWT token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Extrai claims do token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Gera a chave de assinatura
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Retorna tempo de expiração em milissegundos
     */
    public Long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }
}
