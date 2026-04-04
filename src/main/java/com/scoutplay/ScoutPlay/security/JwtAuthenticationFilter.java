package com.scoutplay.ScoutPlay.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para validar JWT token nas requisições
 * Extrai o token do header Authorization e valida
 *
 * Para usar, adicione em SecurityConfig como:
 * http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Endpoints que não precisam de autenticação JWT
     */
    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/login",
        "/api/registro",
        "/",
        "/index.html",
        "/css/",
        "/js/",
        "/img/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Pula validação para endpoints públicos
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Token é válido
                String userId = jwtTokenProvider.extractUserId(token);
                String userType = jwtTokenProvider.extractUserType(token);

                // Pode adicionar informações ao contexto se necessário
                request.setAttribute("userId", userId);
                request.setAttribute("userType", userType);

                filterChain.doFilter(request, response);
            } else {
                // Token inválido
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"Token inválido ou expirado\"}");
            }
        } catch (JwtException ex) {
            log.error("Erro ao processar JWT token: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"errorCode\":\"JWT_ERROR\",\"message\":\"Erro ao processar token\"}");
        }
    }

    /**
     * Extrai token do header Authorization
     * Formato esperado: "Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer "
        }

        return null;
    }

    /**
     * Verifica se o endpoint é público (não precisa de JWT)
     */
    private boolean isPublicEndpoint(String requestUri) {
        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (requestUri.startsWith(publicEndpoint)) {
                return true;
            }
        }
        return false;
    }
}
