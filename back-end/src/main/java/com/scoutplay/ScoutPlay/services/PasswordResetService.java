package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final long TTL_MS = 15 * 60 * 1000; // 15 minutos

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, ResetEntry> tokens = new ConcurrentHashMap<>();

    public String gerarToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            // Retorna sem erro para não vazar quais emails existem
            return null;
        }
        String token = UUID.randomUUID().toString();
        tokens.put(token, new ResetEntry(email, Instant.now().plusMillis(TTL_MS)));
        return token;
    }

    public void redefinirSenha(String token, String novaSenha) {
        ResetEntry entry = tokens.get(token);
        if (entry == null || Instant.now().isAfter(entry.expiry())) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        Usuario usuario = usuarioRepository.findByEmail(entry.email());
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        tokens.remove(token);
    }

    private record ResetEntry(String email, Instant expiry) {}
}
