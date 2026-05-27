package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.AtletaDTO;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.PeDominante;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.AtletaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atletas")
public class AtletaController {

    @Autowired
    private AtletaService atletaService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrarAtleta(@Valid @RequestBody AtletaDTO atletaDTO) {
        Atleta atletaCriado = atletaService.criarAtleta(toAtleta(atletaDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(buildLoginResponse(atletaCriado), "Atleta cadastrado com sucesso"));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Atleta>> criarAtleta(
            @Valid @RequestPart("atleta") AtletaDTO atletaDTO,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) throws IOException {

        Atleta novoAtleta = toAtleta(atletaDTO);

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String fotoCaminho = atletaService.salvarFotoPerfil(fotoPerfil);
            novoAtleta.setFotoPerfil(fotoCaminho);
        }

        Atleta atletaCriado = atletaService.criarAtleta(novoAtleta);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(atletaCriado, "Atleta criado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Atleta>> buscarAtletaPorId(@PathVariable String id) {
        Optional<Atleta> atleta = atletaService.buscarAtletaPorId(id);
        return atleta
                .map(value -> ResponseEntity.ok(ApiResponse.success(value, "Atleta encontrado com sucesso")))
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Atleta>>> listarAtletasComFiltro(
            @RequestParam(value = "name", required = false) String nome,
            @RequestParam(value = "birthYear", required = false) Integer anoNascimento,
            @RequestParam(value = "weight", required = false) Double peso,
            @RequestParam(value = "height", required = false) Double altura,
            @RequestParam(value = "position", required = false) String posicao,
            @RequestParam(value = "dominantFoot", required = false) String dominantFoot,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PeDominante peDominante = (dominantFoot != null && !dominantFoot.isEmpty())
                ? PeDominante.valueOf(dominantFoot.toUpperCase())
                : null;

        Page<Atleta> atletas = atletaService.buscarAtletasComFiltro(
            nome,
            anoNascimento,
            peso,
            altura,
            posicao,
            peDominante,
            PageRequest.of(page, size)
        );
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(atletas), "Atletas listados com sucesso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Atleta>> atualizarInformacoesAtleta(
            @PathVariable String id,
            @Valid @RequestBody AtletaDTO atletaDTO) {

        assertAtletaOwner(id);

        Atleta atleta = atletaService.atualizarAtleta(id, toAtleta(atletaDTO));
        return ResponseEntity.ok(ApiResponse.success(atleta, "Atleta atualizado com sucesso"));
    }

    @PostMapping(path = "/{id}/foto", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Atleta>> atualizarFotoPerfil(
            @PathVariable String id,
            @RequestPart("fotoPerfil") MultipartFile fotoPerfil) throws IOException {

        assertAtletaOwner(id);
        Atleta atleta = atletaService.atualizarFotoPerfil(id, fotoPerfil);
        return ResponseEntity.ok(ApiResponse.success(atleta, "Foto de perfil atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletarAtleta(@PathVariable String id) {
        assertAtletaOwner(id);
        atletaService.deletarAtletaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Atleta removido com sucesso"));
    }

    @GetMapping("/fotos/{filename:.+}")
    public ResponseEntity<Resource> obterFotoPerfil(@PathVariable String filename) {
        try {
            Path caminhoImagem = Paths.get("uploads/fotos_perfil").resolve(filename).normalize();
            Resource imagem = new UrlResource(caminhoImagem.toUri());

            if (imagem.exists() || imagem.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagem.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imagem);
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Atleta toAtleta(AtletaDTO atletaDTO) {
        Atleta atleta = new Atleta();
        atleta.setId(atletaDTO.getId());
        atleta.setNome(atletaDTO.getNome());
        atleta.setTelefone(atletaDTO.getTelefone());
        atleta.setCpf(atletaDTO.getCpf());
        atleta.setDataNascimento(atletaDTO.getDataNascimento());
        atleta.setCep(atletaDTO.getCep());
        atleta.setEmail(atletaDTO.getEmail());
        atleta.setSenha(atletaDTO.getSenha());
        atleta.setPeso(atletaDTO.getPeso());
        atleta.setAltura(atletaDTO.getAltura());
        atleta.setPosicao(atletaDTO.getPosicao());
        atleta.setClubesAnteriores(atletaDTO.getClubesAnteriores());
        atleta.setFotoPerfil(atletaDTO.getFotoPerfil());

        if (atletaDTO.getPeDominante() != null) {
            atleta.setPeDominante(PeDominante.valueOf(atletaDTO.getPeDominante().toUpperCase()));
        }

        if (atletaDTO.getVideos() != null) {
            List<VideoAtleta> videos = atletaDTO.getVideos().stream()
                    .filter(url -> url != null && !url.isBlank())
                    .map(url -> {
                        VideoAtleta videoAtleta = new VideoAtleta();
                        videoAtleta.setTitulo("Video do atleta");
                        videoAtleta.setUrlVideo(url);
                        videoAtleta.setAtleta(atleta);
                        return videoAtleta;
                    })
                    .collect(Collectors.toList());
            atleta.setVideos(videos);
        } else {
            atleta.setVideos(new ArrayList<>());
        }

        return atleta;
    }

    private void assertAtletaOwner(String atletaId) {
        if (!SecurityUtils.isOwner(atletaId)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este atleta.");
        }
    }

    private LoginResponse buildLoginResponse(Atleta atleta) {
        return LoginResponse.builder()
            .token(jwtTokenProvider.generateToken(atleta.getId(), "ATLETA"))
            .userId(atleta.getId())
            .userType("ATLETA")
            .nome(atleta.getNome())
            .email(atleta.getEmail())
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .build();
    }
}