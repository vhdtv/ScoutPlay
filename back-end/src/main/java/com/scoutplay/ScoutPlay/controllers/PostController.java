package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostDataOutputDTO>> criar(@ModelAttribute PostDataInputDTO dto) {
        PostDataOutputDTO criado = this.postService.criar(dto);
        if(criado == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("400", "Erro ao criar post"));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(criado, "Post criado com sucesso"));
    }

    // @GetMapping
    // public ResponseEntity<ApiResponse<PageResponse<PostDataInputDTO>>> listar(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.listar(page, size)));
    // }

    // @GetMapping("/usuario/{autorId}")
    // public ResponseEntity<ApiResponse<PageResponse<PostDataInputDTO>>> listarPorAutor(
    //         @PathVariable UUID autorId,
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.listarPorAutor(autorId, page, size)));
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<ApiResponse<PostDataInputDTO>> buscar(@PathVariable UUID id) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.buscar(id)));
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<ApiResponse<PostDataInputDTO>> atualizar(
    //         @PathVariable UUID id,
    //         @RequestBody PostDataInputDTO dto) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.atualizar(id, dto)));
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable UUID id) {
    //     postService.deletar(id);
    //     return ResponseEntity.ok(ApiResponse.success(null, "Post deletado com sucesso"));
    // }
}
