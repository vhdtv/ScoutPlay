package com.scoutplay.ScoutPlay.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Collection;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || token.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.extractUserId(token);
                Collection<String> userTypes = jwtTokenProvider.extractUserTypes(token);

                request.setAttribute("aliasUsuario", userId);
                request.setAttribute("tipoConta", userTypes);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    userTypes.stream()
                        .map(type -> new SimpleGrantedAuthority("ROLE_" + type))
                        .toList()
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
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer "
        }

        return null;
    }

}
