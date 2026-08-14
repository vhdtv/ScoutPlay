package com.scoutplay.ScoutPlay.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Set<String> SENSITIVE = Set.of(
        "/api/login", "/api/signup", "/api/forgot-password", "/api/reset-password", "/api/ia/prompt"
    );
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if ("POST".equals(request.getMethod()) && SENSITIVE.contains(request.getRequestURI())) {
            int limit = "/api/ia/prompt".equals(request.getRequestURI()) ? 20 : 10;
            String key = request.getRemoteAddr() + ":" + request.getRequestURI();
            long minute = Instant.now().getEpochSecond() / 60;
            Window window = windows.compute(key, (ignored, current) ->
                current == null || current.minute != minute ? new Window(minute, 1) : new Window(minute, current.count + 1));
            if (window.count > limit) {
                response.setStatus(429);
                response.setHeader("Retry-After", "60");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), ApiResponse.error(
                    "RATE_LIMITED", "Muitas tentativas. Tente novamente em instantes."));
                return;
            }
            if (windows.size() > 10_000) windows.entrySet().removeIf(entry -> entry.getValue().minute < minute - 2);
        }
        chain.doFilter(request, response);
    }

    private record Window(long minute, int count) {}
}
