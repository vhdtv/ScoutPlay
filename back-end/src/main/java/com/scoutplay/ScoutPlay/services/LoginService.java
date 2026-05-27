package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.LoginRequest;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final XUsuarioTipoContaRepository xUsuarioTipoContaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(passwordEncoder.encode(request.getSenha()), usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        XUsuarioTipoConta relacao = xUsuarioTipoContaRepository.getByUsuario(usuario);
        String userType = resolverTipo(relacao);

        String userId = usuario.getAliasId().toString();
        String token = jwtTokenProvider.generateToken(userId, userType);

        return LoginResponse.builder()
                .token(token)
                .userId(userId)
                .userType(userType)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .build();
    }

    public LoginResponse buscarPorUserId(String userId) {
        UUID aliasId = UUID.fromString(userId);
        Usuario usuario = usuarioRepository.findByAliasId(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        XUsuarioTipoConta relacao = xUsuarioTipoContaRepository.getByUsuario(usuario);
        String userType = resolverTipo(relacao);
        return LoginResponse.builder()
                .userId(userId)
                .userType(userType)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }

    private String resolverTipo(XUsuarioTipoConta relacao) {
        if (relacao == null) return "USUARIO";
        return switch (relacao.getTipoConta().getId()) {
            case TipoConta.ATLETA -> "ATLETA";
            case TipoConta.OLHEIRO -> "OLHEIRO";
            case TipoConta.RESPONSAVEL -> "RESPONSAVEL";
            case TipoConta.REPRESENTANTE_CLUBE -> "REPRESENTANTE_CLUBE";
            default -> "USUARIO";
        };
    }
}
