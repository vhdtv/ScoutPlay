package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.VideoDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.services.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * POST /api/atletas/{atletaId}/videos
     * Adiciona vídeo ao perfil do atleta. Apenas o próprio atleta pode adicionar.
     * Body: { "urlVideo": "https://...", "titulo": "..." }
     */
    @PostMapping("/api/atletas/{atletaId}/videos")
    public ResponseEntity<ApiResponse<VideoDTO>> adicionar(
            @PathVariable UUID atletaId,
            @Valid @RequestBody VideoDTO dto) {
        VideoDTO salvo = videoService.adicionar(atletaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(salvo, "Vídeo adicionado com sucesso"));
    }

    /**
     * GET /api/atletas/{atletaId}/videos?page=0&size=5
     * Lista vídeos do atleta paginado. Qualquer usuário autenticado pode listar.
     */
    @GetMapping("/api/atletas/{atletaId}/videos")
    public ResponseEntity<ApiResponse<PageResponse<VideoDTO>>> listar(
            @PathVariable UUID atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success(videoService.listar(atletaId, page, size)));
    }

    /**
     * PUT /api/videos/{videoId}
     * Atualiza título ou URL de um vídeo. Apenas o dono pode atualizar.
     * Body: { "urlVideo": "...", "titulo": "..." } — campos são opcionais.
     */
    @PutMapping("/api/videos/{videoId}")
    public ResponseEntity<ApiResponse<VideoDTO>> atualizar(
            @PathVariable UUID videoId,
            @RequestBody VideoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(videoService.atualizar(videoId, dto)));
    }

    /**
     * DELETE /api/videos/{videoId}
     * Remove um vídeo. Apenas o dono pode deletar.
     */
    @DeleteMapping("/api/videos/{videoId}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable UUID videoId) {
        videoService.deletar(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Vídeo removido com sucesso"));
    }
}
