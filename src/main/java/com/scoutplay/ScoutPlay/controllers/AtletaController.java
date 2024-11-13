package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.DTO.AtletaDTO;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.PeDominante;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.services.AtletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atletas")
public class AtletaController {

    @Autowired
    private AtletaService atletaService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> criarAtleta(
            @RequestPart("atleta") AtletaDTO atletaDTO,
            @RequestPart("fotoPerfil") MultipartFile fotoPerfil) {
        try {
            // Verifique a duplicidade de e-mail
            if (atletaService.existeEmail(atletaDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Este e-mail já está em uso. Por favor, utilize outro.");
            }

            // Converter AtletaDTO para Atleta
            Atleta novoAtleta = new Atleta();
            novoAtleta.setNome(atletaDTO.getNome());
            novoAtleta.setTelefone(atletaDTO.getTelefone());
            novoAtleta.setCpf(atletaDTO.getCpf());
            novoAtleta.setDataNascimento(LocalDate.parse(atletaDTO.getDataNascimento()));
            novoAtleta.setCep(atletaDTO.getCep());
            novoAtleta.setEmail(atletaDTO.getEmail());
            novoAtleta.setSenha(atletaDTO.getSenha());
            novoAtleta.setPeso(atletaDTO.getPeso());
            novoAtleta.setAltura(atletaDTO.getAltura());
            novoAtleta.setPosicao(atletaDTO.getPosicao());
            novoAtleta.setClubesAnteriores(atletaDTO.getClubesAnteriores());
            novoAtleta.setPeDominante(PeDominante.valueOf(atletaDTO.getPeDominante()));

            // Salvar a foto de perfil se ela existir
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                String fotoCaminho = atletaService.salvarFotoPerfil(fotoPerfil);
                novoAtleta.setFotoPerfil(fotoCaminho);
            }

            // Converter URLs de vídeo para objetos VideoAtleta e associar ao atleta
            if (atletaDTO.getVideos() != null) {
                List<VideoAtleta> videos = atletaDTO.getVideos().stream()
                        .map(url -> {
                            VideoAtleta videoAtleta = new VideoAtleta();
                            videoAtleta.setUrlVideo(url);
                            videoAtleta.setAtleta(novoAtleta);
                            return videoAtleta;
                        }).collect(Collectors.toList());
                novoAtleta.setVideos(videos);
            }

            // Salva o atleta com a lista de vídeos associada
            Atleta atletaCriado = atletaService.criarAtleta(novoAtleta);
            return new ResponseEntity<>(atletaCriado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atleta> buscarAtletaPorId(@PathVariable String id) {
        Optional<Atleta> atleta = atletaService.buscarAtletaPorId(id);
        return atleta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<Atleta> listarAtletasComFiltro(
            @RequestParam(value = "name", required = false) String nome,
            @RequestParam(value = "birthYear", required = false) Integer anoNascimento,
            @RequestParam(value = "weight", required = false) Double peso,
            @RequestParam(value = "height", required = false) Double altura,
            @RequestParam(value = "position", required = false) String posicao) {

        return atletaService.buscarAtletasComFiltro(nome, anoNascimento, peso, altura, posicao);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Atleta> atualizarInformacoesAtleta(@PathVariable String id, @RequestBody Atleta atualizarInformacoesAtleta) {
        try {
            Atleta atleta = atletaService.atualizarAtleta(id, atualizarInformacoesAtleta);
            return ResponseEntity.ok(atleta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtleta(@PathVariable String id) {
        atletaService.deletarAtletaPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fotos/{filename:.+}")
    public ResponseEntity<Resource> obterFotoPerfil(@PathVariable String filename) {
        try {
            Path caminhoImagem = Paths.get("uploads/fotos_perfil").resolve(filename).normalize();
            Resource imagem = new UrlResource(caminhoImagem.toUri());

            if (imagem.exists() || imagem.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagem.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)  // Ajuste o tipo de mídia conforme necessário
                        .body(imagem);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
