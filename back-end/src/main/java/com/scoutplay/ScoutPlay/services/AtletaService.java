package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.AtletaDTO;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.UserSummary;
import com.scoutplay.ScoutPlay.api.dto.VideoDTO;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.VideoAtletaRepository;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtletaService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final DetalhePerfilRepository detalhePerfilRepository;
    private final DetalhePerfilService detalhePerfilService;
    private final VideoAtletaRepository videoAtletaRepository;
    private final TipoContaService tipoContaService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public LoginResponse registrar(AtletaDTO dto) {
        Usuario usuario = new Usuario(
                dto.getNome(), "",
                dto.getEmail(), dto.getCpf(),
                dto.getSenha(), dto.getDataNascimento()
        );
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());

        Usuario salvo = usuarioService.cadastrarAtleta(usuario);

        Map<String, Object> detalhes = camposEspecificos(dto);
        if (!detalhes.isEmpty()) detalhePerfilService.adicionarInformacao(detalhes, salvo);

        String userId = salvo.getAliasId().toString();
        String token = jwtTokenProvider.generateToken(userId, "ATLETA");
        return LoginResponse.builder()
                .tokenAcesso(token)
                .expiraEm(jwtTokenProvider.getExpirationMs())
                .usuario(UserSummary.builder()
                    .tipoConta("ATLETA")
                    .nome(salvo.getNome())
                    .build()
                )
                .build();
    }

    public AtletaDTO buscar(UUID aliasId) {
        Usuario usuario = resolverAtleta(aliasId);
        return toDTO(usuario);
    }

    public PageResponse<AtletaDTO> listar(int page, int size,
            String posicao, Double alturaMin, Double alturaMax,
            Double pesoMin, Double pesoMax) {
        boolean semFiltros = posicao == null && alturaMin == null && alturaMax == null
                && pesoMin == null && pesoMax == null;
        if (semFiltros) {
            Pageable pageable = PageRequest.of(page, size);
            Page<AtletaDTO> resultado = usuarioRepository
                    .findAllAtivosByTipoContaId(TipoConta.ATLETA, pageable)
                    .map(this::toDTO);
            return PageResponse.fromPage(resultado);
        }

        List<AtletaDTO> todos = usuarioRepository
                .findAllAtivosByTipoContaId(TipoConta.ATLETA, Pageable.unpaged())
                .stream()
                .map(this::toDTO)
                .filter(a -> posicao == null || posicao.equalsIgnoreCase(a.getPosicao()))
                .filter(a -> alturaMin == null || (a.getAltura() != null && a.getAltura() >= alturaMin))
                .filter(a -> alturaMax == null || (a.getAltura() != null && a.getAltura() <= alturaMax))
                .filter(a -> pesoMin == null || (a.getPeso() != null && a.getPeso() >= pesoMin))
                .filter(a -> pesoMax == null || (a.getPeso() != null && a.getPeso() <= pesoMax))
                .toList();

        int total = todos.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<AtletaDTO> pagina = todos.subList(fromIndex, toIndex);

        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) total / size);
        return PageResponse.<AtletaDTO>builder()
                .content(pagina)
                .currentPage(page)
                .pageSize(size)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(toIndex < total)
                .hasPrevious(page > 0)
                .build();
    }

    @Transactional
    public AtletaDTO atualizar(UUID aliasId, AtletaDTO dto) {
        if (!SecurityUtils.isOwner(aliasId.toString())) {
            throw new AccessDeniedException("Você não tem permissão para editar este perfil");
        }
        Usuario usuario = resolverAtleta(aliasId);

        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());
        usuarioRepository.save(usuario);

        Map<String, Object> detalhes = camposEspecificos(dto);
        if (!detalhes.isEmpty()) detalhePerfilService.adicionarInformacao(detalhes, usuario);

        return toDTO(usuario);
    }

    @Transactional
    public String salvarFoto(UUID aliasId, MultipartFile file) throws IOException {
        if (!SecurityUtils.isOwner(aliasId.toString())) {
            throw new AccessDeniedException("Você não tem permissão para editar este perfil");
        }
        Usuario usuario = resolverAtleta(aliasId);

        String contentType = file.getContentType();
        if (contentType == null || !List.of("image/jpeg", "image/jpg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Formato inválido. Use JPG ou PNG.");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String ext = extensao(file.getOriginalFilename());
        String filename = aliasId + "_" + System.currentTimeMillis() + ext;
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        usuario.setFotoPerfil(filename);
        usuarioRepository.save(usuario);
        return filename;
    }

    @Transactional
    public void deletar(UUID aliasId) {
        if (!SecurityUtils.isOwner(aliasId.toString())) {
            throw new AccessDeniedException("Você não tem permissão para deletar este perfil");
        }
        Usuario usuario = resolverAtleta(aliasId);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    // -------------------------------------------------------------------------

    private Usuario resolverAtleta(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado"));
        if (!tipoContaService.verificarTipoConta(usuario, TipoConta.ATLETA)) {
            throw new ResourceNotFoundException("Atleta não encontrado");
        }
        return usuario;
    }

    private AtletaDTO toDTO(Usuario usuario) {
        DetalhePerfil detalhes = detalhePerfilRepository.getByUsuario(usuario);
        Map<String, Object> data = detalhes != null ? detalhes.getData() : new HashMap<>();

        List<VideoDTO> videos = videoAtletaRepository
                .findByUsuario(usuario, Pageable.unpaged())
                .stream()
                .map(v -> VideoDTO.builder()
                        .id(v.getAliasId())
                        .urlVideo(v.getUrlVideo())
                        .titulo(v.getTitulo())
                        .dataCriacao(v.getDataCriacao())
                        .build())
                .toList();

        return AtletaDTO.builder()
                .id(usuario.getAliasId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .dataNascimento(usuario.getDataNascimento())
                .idade(usuario.obterIdade())
                .fotoPerfil(usuario.getFotoPerfil())
                .posicao(getString(data, "posicao"))
                .peso(getDouble(data, "peso"))
                .altura(getDouble(data, "altura"))
                .peDominante(getString(data, "peDominante"))
                .cep(getString(data, "cep"))
                .clubesAnteriores(getString(data, "clubesAnteriores"))
                .videos(videos)
                .build();
    }

    private Map<String, Object> camposEspecificos(AtletaDTO dto) {
        Map<String, Object> m = new HashMap<>();
        if (dto.getAltura() != null)           m.put("altura", dto.getAltura());
        if (dto.getPeso() != null)             m.put("peso", dto.getPeso());
        if (dto.getPosicao() != null)          m.put("posicao", dto.getPosicao());
        if (dto.getPeDominante() != null)      m.put("peDominante", dto.getPeDominante());
        if (dto.getCep() != null)              m.put("cep", dto.getCep());
        if (dto.getClubesAnteriores() != null) m.put("clubesAnteriores", dto.getClubesAnteriores());
        return m;
    }

    private String getString(Map<String, Object> data, String key) {
        Object v = data.get(key);
        return v != null ? v.toString() : null;
    }

    private Double getDouble(Map<String, Object> data, String key) {
        Object v = data.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }

    private String extensao(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
