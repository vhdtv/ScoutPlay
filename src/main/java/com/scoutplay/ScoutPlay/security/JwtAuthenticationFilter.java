package com.scoutplay.ScoutPlay.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
    private static final String[] PUBLIC_API_ENDPOINTS = {
        "/api/login",
        "/api/atletas/registro",
        "/api/olheiros/registro",
        "/api/responsaveis"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Pula validação para endpoints públicos
        String requestUri = request.getRequestURI();
        if (isPublicEndpoint(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.extractUserId(token);
                String userType = jwtTokenProvider.extractUserType(token);

                request.setAttribute("userId", userId);
                request.setAttribute("userType", userType);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + userType))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } else {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"Token inválido ou expirado\"}");
            }
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
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
        if (!requestUri.startsWith("/api/")) {
            return true;
        }
        if (requestUri.startsWith("/api/atletas/fotos/")) {
            return true;
        }
        for (String publicEndpoint : PUBLIC_API_ENDPOINTS) {
            if (requestUri.startsWith(publicEndpoint)) {
                return true;
            }
        }
        return false;
    }
}
