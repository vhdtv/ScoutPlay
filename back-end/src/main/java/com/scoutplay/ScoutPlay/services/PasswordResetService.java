package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.PasswordResetToken;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.PasswordResetTokenRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Duration REQUEST_INTERVAL = Duration.ofMinutes(1);

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.ttl-minutes:30}")
    private long ttlMinutes;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public void solicitarRecuperacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return;
        }

        Instant agora = Instant.now();
        if (tokenRepository.existsByUsuarioAndCriadoEmAfter(usuario, agora.minus(REQUEST_INTERVAL))) {
            return;
        }

        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokenRepository.save(new PasswordResetToken(
            usuario,
            hashToken(token),
            agora,
            agora.plus(Duration.ofMinutes(ttlMinutes))
        ));
        emailService.enviarLinkRecuperacao(email, frontendUrl + "/reset-password?token=" + token);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token de recuperação inválido ou expirado");
        }
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres");
        }

        Instant agora = Instant.now();
        PasswordResetToken resetToken = tokenRepository
            .findByTokenHashAndUsadoEmIsNull(hashToken(token))
            .filter(item -> item.podeSerUsado(agora))
            .orElseThrow(() -> new IllegalArgumentException("Token de recuperação inválido ou expirado"));

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        resetToken.marcarComoUsado(agora);
    }

    static String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}
