package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final XUsuarioTipoContaRepository xUsuarioTipoContaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public ClientLoginOutputDTO autenticarUsuario(ClientLoginInputDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) throw new IllegalArgumentException("Email ou senha inválidos");
        Set<XUsuarioTipoConta> poderesQueContaPossui = xUsuarioTipoContaRepository.getByUsuario(usuario);
        String[] poderesConta = poderesQueContaPossui.stream().map(poder -> UsuarioService.resolverTipo(poder)).toArray(String[]::new);
        
        String aliasUsuario = usuario.getAliasId().toString();
        String authToken = jwtTokenProvider.generateToken(aliasUsuario, poderesConta[0]);

        return ClientLoginOutputDTO.builder()
            .tokenAcesso(authToken)
            .expiraEm(jwtTokenProvider.getExpirationMs())
            .usuario(UserSummaryDTO.builder()
                .nome(usuario.getNome())
                .iniciais(usuario.getIniciais())
                .username(usuario.getUsername())
                .sobrenome(usuario.getSobrenome())
                .fotoPerfil(usuario.getFotoPerfil())
                .build()
            ).build();
    }
}
