package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.VideoDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/atletas/{atletaId}/videos")
    public ResponseEntity<ApiResponse<PageResponse<VideoAtleta>>> listarVideos(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<VideoAtleta> videos = videoService.listarPorAtleta(atletaId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(videos), "Vídeos listados com sucesso"));
    }

    @PostMapping("/atletas/{atletaId}/videos")
    public ResponseEntity<ApiResponse<VideoAtleta>> adicionarVideo(
            @PathVariable String atletaId,
            @Valid @RequestBody VideoDTO videoDTO) {

        assertAtletaOwner(atletaId);
        VideoAtleta video = videoService.adicionarVideo(atletaId, videoDTO);
        return ResponseEntity.ok(ApiResponse.success(video, "Vídeo adicionado com sucesso"));
    }

    @PutMapping("/videos/{videoId}")
    public ResponseEntity<ApiResponse<VideoAtleta>> atualizarVideo(
            @PathVariable UUID videoId,
            @Valid @RequestBody VideoDTO videoDTO) {

        VideoAtleta videoAtual = videoService.buscarPorId(videoId);
        assertAtletaOwner(videoAtual.getAtleta().getId());
        VideoAtleta video = videoService.atualizarVideo(videoId, videoDTO);
        return ResponseEntity.ok(ApiResponse.success(video, "Vídeo atualizado com sucesso"));
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<ApiResponse<Void>> deletarVideo(@PathVariable UUID videoId) {
        VideoAtleta videoAtual = videoService.buscarPorId(videoId);
        assertAtletaOwner(videoAtual.getAtleta().getId());
        videoService.deletarVideo(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Vídeo removido com sucesso"));
    }

    private void assertAtletaOwner(String atletaId) {
        if (!SecurityUtils.isOwner(atletaId)) {
            throw new AccessDeniedException("Você não tem permissão para alterar os vídeos deste atleta.");
        }
    }
}