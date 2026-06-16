package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SENHA_LENGTH = 10;
    private static final SecureRandom RNG = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void enviarSenhaTemporaria(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            // Silencioso — não vaza quais e-mails existem
            return;
        }
        String novaSenha = gerarSenhaAleatoria();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        emailService.enviarNovaSenha(email, novaSenha);
    }

    private String gerarSenhaAleatoria() {
        StringBuilder sb = new StringBuilder(SENHA_LENGTH);
        for (int i = 0; i < SENHA_LENGTH; i++) {
            sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
