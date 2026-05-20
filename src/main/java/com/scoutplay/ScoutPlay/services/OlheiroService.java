package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.OlheiroDTO;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OlheiroService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final DetalhePerfilRepository detalhePerfilRepository;
    private final DetalhePerfilService detalhePerfilService;
    private final TipoContaService tipoContaService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse registrar(OlheiroDTO dto) {
        Usuario usuario = new Usuario(
                dto.getNome(), "",
                dto.getEmail(), dto.getCpf(),
                dto.getSenha(), dto.getDataNascimento()
        );
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());

        Usuario salvo = usuarioService.cadastrarOlheiro(usuario);

        Map<String, Object> detalhes = camposEspecificos(dto);
        if (!detalhes.isEmpty()) detalhePerfilService.adicionarInformacao(detalhes, salvo);

        String userId = salvo.getAliasId().toString();
        String token = jwtTokenProvider.generateToken(userId, "OLHEIRO");
        return LoginResponse.builder()
                .token(token).userId(userId).userType("OLHEIRO")
                .nome(salvo.getNome()).email(salvo.getEmail())
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .build();
    }

    public OlheiroDTO buscar(UUID aliasId) {
        return toDTO(resolverOlheiro(aliasId));
    }

    public PageResponse<OlheiroDTO> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OlheiroDTO> resultado = usuarioRepository
                .findAllAtivosByTipoContaId(TipoConta.OLHEIRO, pageable)
                .map(this::toDTO);
        return PageResponse.fromPage(resultado);
    }

    @Transactional
    public OlheiroDTO atualizar(UUID aliasId, OlheiroDTO dto) {
        if (!SecurityUtils.isOwner(aliasId.toString())) {
            throw new AccessDeniedException("Você não tem permissão para editar este perfil");
        }
        Usuario usuario = resolverOlheiro(aliasId);

        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());
        usuarioRepository.save(usuario);

        Map<String, Object> detalhes = camposEspecificos(dto);
        if (!detalhes.isEmpty()) detalhePerfilService.adicionarInformacao(detalhes, usuario);

        return toDTO(usuario);
    }

    // -------------------------------------------------------------------------

    private Usuario resolverOlheiro(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado"));
        if (!tipoContaService.verificarTipoConta(usuario, TipoConta.OLHEIRO)) {
            throw new ResourceNotFoundException("Olheiro não encontrado");
        }
        return usuario;
    }

    private OlheiroDTO toDTO(Usuario usuario) {
        DetalhePerfil detalhes = detalhePerfilRepository.getByUsuario(usuario);
        Map<String, Object> data = detalhes != null ? detalhes.getData() : new HashMap<>();

        return OlheiroDTO.builder()
                .id(usuario.getAliasId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .dataNascimento(usuario.getDataNascimento())
                .cep(getString(data, "cep"))
                .clube(getString(data, "clube"))
                .local(getString(data, "local"))
                .build();
    }

    private Map<String, Object> camposEspecificos(OlheiroDTO dto) {
        Map<String, Object> m = new HashMap<>();
        if (dto.getCep() != null)   m.put("cep", dto.getCep());
        if (dto.getClube() != null) m.put("clube", dto.getClube());
        if (dto.getLocal() != null) m.put("local", dto.getLocal());
        return m;
    }

    private String getString(Map<String, Object> data, String key) {
        Object v = data.get(key);
        return v != null ? v.toString() : null;
    }
}
