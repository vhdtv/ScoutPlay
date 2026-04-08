package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.AvaliacaoDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/atletas/{atletaId}/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Avaliacao>>> listar(@PathVariable String atletaId) {
        return ResponseEntity.ok(ApiResponse.success(avaliacaoService.listarPorAtleta(atletaId), "Avaliações listadas com sucesso"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Avaliacao>> criar(
            @PathVariable String atletaId,
            @Valid @RequestBody AvaliacaoDTO dto) {

        if (!SecurityUtils.hasUserType("OLHEIRO")) {
            throw new AccessDeniedException("Somente olheiros podem cadastrar avaliações.");
        }

        Avaliacao avaliacao = avaliacaoService.criar(atletaId, SecurityUtils.currentUserId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(avaliacao, "Avaliação criada com sucesso"));
    }
}