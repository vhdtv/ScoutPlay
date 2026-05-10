package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.AvaliacaoDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/atletas/{atletaId}/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Avaliacao>>> listar(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Avaliacao> avaliacaoPage = avaliacaoService.listarPorAtleta(
            atletaId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(
            PageResponse.fromPage(avaliacaoPage), "Avaliações listadas com sucesso"));
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

    @PutMapping("/{avaliacaoId}")
    public ResponseEntity<ApiResponse<Avaliacao>> atualizar(
            @PathVariable String atletaId,
            @PathVariable UUID avaliacaoId,
            @Valid @RequestBody AvaliacaoDTO dto) {

        if (!SecurityUtils.hasUserType("OLHEIRO")) {
            throw new AccessDeniedException("Somente olheiros podem editar avaliações.");
        }

        Avaliacao existente = avaliacaoService.buscarPorId(avaliacaoId);
        if (!existente.getOlheiro().getId().equals(SecurityUtils.currentUserId())) {
            throw new AccessDeniedException("Você não tem permissão para editar esta avaliação.");
        }

        Avaliacao atualizada = avaliacaoService.atualizar(avaliacaoId, dto);
        return ResponseEntity.ok(ApiResponse.success(atualizada, "Avaliação atualizada com sucesso"));
    }

    @DeleteMapping("/{avaliacaoId}")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @PathVariable String atletaId,
            @PathVariable UUID avaliacaoId) {

        if (!SecurityUtils.hasUserType("OLHEIRO")) {
            throw new AccessDeniedException("Somente olheiros podem remover avaliações.");
        }

        Avaliacao existente = avaliacaoService.buscarPorId(avaliacaoId);
        if (!existente.getOlheiro().getId().equals(SecurityUtils.currentUserId())) {
            throw new AccessDeniedException("Você não tem permissão para remover esta avaliação.");
        }

        avaliacaoService.deletar(avaliacaoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Avaliação removida com sucesso"));
    }
}
