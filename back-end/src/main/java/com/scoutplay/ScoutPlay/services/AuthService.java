package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientSignupInputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public ClientLoginOutputDTO autenticarUsuario(ClientLoginInputDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) throw new IllegalArgumentException("Email ou senha inválidos");
        String[] poderesConta = usuario.getPoderesConta().stream().map(poder -> poder.getNome()).toArray(String[]::new);
        
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

    public ClientLoginInputDTO cadastrar(ClientSignupInputDTO dto) throws Exception {
        Usuario usuario = new Usuario(dto.getNome(), dto.getSobrenome(), dto.getEmail(), dto.getCpf(), dto.getSenha(), dto.getDataNascimento());
        if(dto.getUsername() != null) usuario.setUsername(dto.getUsername());
        switch(dto.getTipoConta().toUpperCase()){
            case "ATLETA":
                usuarioService.cadastrarAtleta(usuario);
                break;
            case "OLHEIRO":
                usuarioService.cadastrarOlheiro(usuario);
                break;
            case "RESPONSAVEL":
                usuarioService.cadastrarResponsavel(usuario);
                break;
            default:
                throw new Exception("O tipo da conta precisa ser definido");
        }
        return ClientLoginInputDTO.builder()
            .email(dto.getEmail())
            .senha(dto.getSenha())
            .build();
    }
}
