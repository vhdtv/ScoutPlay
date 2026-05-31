package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.LoginRequest;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.UserSummary;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final XUsuarioTipoContaRepository xUsuarioTipoContaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse autenticarUsuario(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) throw new IllegalArgumentException("Email ou senha inválidos");
        XUsuarioTipoConta relacao = xUsuarioTipoContaRepository.getByUsuario(usuario);
        
        String aliasUsuario = usuario.getAliasId().toString();
        String tipoConta = resolverTipo(relacao);
        String authToken = jwtTokenProvider.generateToken(aliasUsuario, tipoConta);

        int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();

        return LoginResponse.builder()
            .tokenAcesso(authToken)
            .expiraEm(jwtTokenProvider.getExpirationMs())
            .usuario(UserSummary.builder()
                .nome(usuario.getNome())
                .nomeUsuario(usuario.getEnderecoUnico())
                .email(usuario.getEmail())
                .sobrenome(usuario.getSobrenome())
                .fotoPerfil(usuario.getFotoPerfil())
                .tipoConta(tipoConta)
                .idade(idade)
                .build()
            ).build();
    }

    public UserSummary buscarUsuarioPorAliasId(UUID aliasId) {
        Usuario usuario = usuarioRepository
                .findByAliasId(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        XUsuarioTipoConta relacao = xUsuarioTipoContaRepository.getByUsuario(usuario);
        String tipoConta = resolverTipo(relacao);
        int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();

        return UserSummary.builder()
            .nome(usuario.getNome())
            .nomeUsuario(usuario.getEnderecoUnico())
            .email(usuario.getEmail())
            .sobrenome(usuario.getSobrenome())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(tipoConta)
            .idade(idade).build();
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
