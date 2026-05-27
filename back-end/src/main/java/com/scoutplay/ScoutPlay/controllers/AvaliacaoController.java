package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.AvaliacaoDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.services.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    /**
     * POST /api/avaliacoes
     * Olheiro avalia um atleta. Requer token de OLHEIRO.
     * Body: { atletaId, nota, comentario?, videoId? }
     */
    @PostMapping("/api/avaliacoes")
    public ResponseEntity<ApiResponse<AvaliacaoDTO>> criar(@Valid @RequestBody AvaliacaoDTO dto) {
        AvaliacaoDTO salva = avaliacaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(salva, "Avaliação criada com sucesso"));
    }

    /**
     * GET /api/atletas/{atletaId}/avaliacoes?page=0&size=10
     * Lista avaliações recebidas pelo atleta. Qualquer usuário autenticado pode ver.
     */
    @GetMapping("/api/atletas/{atletaId}/avaliacoes")
    public ResponseEntity<ApiResponse<PageResponse<AvaliacaoDTO>>> listar(
            @PathVariable UUID atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(avaliacaoService.listarPorAtleta(atletaId, page, size)));
    }
}
