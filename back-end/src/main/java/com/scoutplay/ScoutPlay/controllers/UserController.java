package com.scoutplay.ScoutPlay.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scoutplay.ScoutPlay.api.dto.AtletaCardDTO;
import com.scoutplay.ScoutPlay.api.dto.AvaliacaoInputDTO;
import com.scoutplay.ScoutPlay.api.dto.AvaliacaoOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.DetalhePerfilOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.ProfileDetailInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ProfileDetailRemoveInputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileFieldsDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.AvaliacaoService;
import com.scoutplay.ScoutPlay.services.FileService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UsuarioService usuarioService;
    private final AvaliacaoService avaliacaoService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/profile-detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> registrarNovoDetalhePerfil(@Valid @RequestBody ProfileDetailInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.adicionarInformacao(body.getChave(), body.getValor(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }

    @DeleteMapping("/profile-detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> deletarDetalhePerfil(@Valid @RequestBody ProfileDetailRemoveInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.removerInformacao(body.getChave(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }
    
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserProfileSummary>> getUserData(@RequestParam String user, @CookieValue(name = "access_token", required = false) String accessToken) throws IllegalArgumentException {
        UserProfileSummary userData;
        if(accessToken == null || accessToken.isEmpty() || accessToken.isBlank()) {
            userData = usuarioService.buscarDadosPerfil(user);
        }
        else {
            UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
            userData = usuarioService.buscarDadosPerfil(user, aliasId);
        }
        return ResponseEntity.ok(ApiResponse.success(userData));
    }

    
    @PostMapping("/user/{username}/seguir")
    public ResponseEntity<ApiResponse<Boolean>> seguir(
            @PathVariable String username,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        boolean ok = usuarioService.seguir(aliasId, username);
        return ResponseEntity.ok(ApiResponse.success(ok));
    }

    @DeleteMapping("/user/{username}/seguir")
    public ResponseEntity<ApiResponse<Boolean>> pararDeSeguir(
            @PathVariable String username,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        boolean ok = usuarioService.pararDeSeguir(aliasId, username);
        return ResponseEntity.ok(ApiResponse.success(ok));
    }

    @PostMapping(value = "/user/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserSummaryDTO>> atualizarFotoPerfil(
            @RequestParam("arquivo") MultipartFile arquivo,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        Usuario usuario = usuarioService.buscarPor(aliasId);
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("uploads/avatars"));
            String filename = FileService.saveFileInFolder(arquivo, "uploads/avatars/");
            usuario.setFotoPerfil(filename);
            usuarioService.salvar(usuario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("500", "Erro ao salvar avatar"));
        }
        return ResponseEntity.ok(ApiResponse.success(UserSummaryDTO.builder()
            .nome(usuario.getNome())
            .sobrenome(usuario.getSobrenome())
            .username(usuario.getUsername())
            .iniciais(usuario.getIniciais())
            .fotoPerfil(usuario.getFotoPerfil())
            .build()));
    }

    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<DetalhePerfil>> atualizarParcialmente(@Valid @RequestBody UserProfileFieldsDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.atualizarConfiguracoesPerfilParcialmente(body.getConfig().get(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(registro));
    }

    @PatchMapping("/user/info")
    public ResponseEntity<ApiResponse<com.scoutplay.ScoutPlay.api.dto.UserProfileSummary>> atualizarInfoPerfil(
            @RequestBody UserProfileFieldsDTO body,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        com.scoutplay.ScoutPlay.api.dto.UserProfileSummary resultado =
            usuarioService.atualizarPerfilParcialmente(body, usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    // ── Avaliações ────────────────────────────────────────────────────────────

    @PostMapping("/user/{username}/avaliar")
    public ResponseEntity<ApiResponse<AvaliacaoOutputDTO>> avaliarAtleta(
            @PathVariable String username,
            @Valid @RequestBody AvaliacaoInputDTO body,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID olheiroId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        Usuario olheiro = usuarioService.buscarPor(olheiroId);
        Avaliacao avaliacao = avaliacaoService.criar(olheiro, username, body.getNota(), body.getComentario());
        return ResponseEntity.ok(ApiResponse.success(toAvaliacaoDTO(avaliacao)));
    }

    @GetMapping("/user/{username}/avaliacoes")
    public ResponseEntity<ApiResponse<List<AvaliacaoOutputDTO>>> listarAvaliacoes(
            @PathVariable String username) {
        List<AvaliacaoOutputDTO> lista = avaliacaoService.buscarPorAtleta(username).stream()
            .map(this::toAvaliacaoDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(lista));
    }

    private AvaliacaoOutputDTO toAvaliacaoDTO(Avaliacao a) {
        Usuario o = a.getOlheiro();
        return AvaliacaoOutputDTO.builder()
            .id(a.getAliasId())
            .nota(a.getNota())
            .comentario(a.getComentario())
            .criadoEm(a.getCriadoEm())
            .olheiro(UserSummaryDTO.builder()
                .nome(o.getNome())
                .sobrenome(o.getSobrenome())
                .username(o.getUsername())
                .iniciais(o.getIniciais())
                .fotoPerfil(o.getFotoPerfil())
                .build())
            .build();
    }

    // ── Shortlist ─────────────────────────────────────────────────────────────

    @PostMapping("/user/{username}/shortlist")
    public ResponseEntity<ApiResponse<Void>> adicionarShortlist(
            @PathVariable String username,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID olheiroId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        usuarioService.adicionarAShortlist(olheiroId, username);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/user/{username}/shortlist")
    public ResponseEntity<ApiResponse<Void>> removerShortlist(
            @PathVariable String username,
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID olheiroId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        usuarioService.removerDaShortlist(olheiroId, username);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/shortlist")
    public ResponseEntity<ApiResponse<List<AtletaCardDTO>>> obterShortlist(
            @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID olheiroId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        return ResponseEntity.ok(ApiResponse.success(usuarioService.obterShortlist(olheiroId)));
    }

    // ── Busca de atletas ──────────────────────────────────────────────────────

    @GetMapping("/atletas")
    public ResponseEntity<ApiResponse<List<AtletaCardDTO>>> buscarAtletas(
            @RequestParam(required = false) String posicao,
            @RequestParam(required = false) String peDominante,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(
            usuarioService.buscarAtletas(posicao, peDominante, nome, page)));
    }

}
