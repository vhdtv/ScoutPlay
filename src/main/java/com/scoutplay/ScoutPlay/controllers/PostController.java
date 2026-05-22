package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.PostDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDTO>> criar(@Valid @RequestBody PostDTO dto) {
        PostDTO criado = postService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(criado, "Post criado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.listar(page, size)));
    }

    @GetMapping("/usuario/{autorId}")
    public ResponseEntity<ApiResponse<PageResponse<PostDTO>>> listarPorAutor(
            @PathVariable UUID autorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.listarPorAutor(autorId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(postService.buscar(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> atualizar(
            @PathVariable UUID id,
            @RequestBody PostDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(postService.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable UUID id) {
        postService.deletar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Post deletado com sucesso"));
    }
}
