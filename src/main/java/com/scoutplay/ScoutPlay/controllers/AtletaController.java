package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.AtletaDTO;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.services.AtletaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/atletas")
@RequiredArgsConstructor
public class AtletaController {

    private final AtletaService atletaService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * POST /api/atletas/registro
     * Cria conta de atleta e retorna token JWT imediatamente.
     * Body: AtletaDTO (nome, email, senha, cpf, dataNascimento obrigatórios)
     */
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrar(@Valid @RequestBody AtletaDTO dto) {
        LoginResponse response = atletaService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Atleta registrado com sucesso"));
    }

    /**
     * GET /api/atletas?page=0&size=10
     * Lista atletas paginado. Requer autenticação.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AtletaDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String posicao,
            @RequestParam(required = false) Double alturaMin,
            @RequestParam(required = false) Double alturaMax,
            @RequestParam(required = false) Double pesoMin,
            @RequestParam(required = false) Double pesoMax) {
        return ResponseEntity.ok(ApiResponse.success(
                atletaService.listar(page, size, posicao, alturaMin, alturaMax, pesoMin, pesoMax)));
    }

    /**
     * GET /api/atletas/{id}
     * Retorna perfil completo do atleta incluindo vídeos.
     * O {id} é o aliasId (UUID) retornado no login/registro.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AtletaDTO>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(atletaService.buscar(id)));
    }

    /**
     * PUT /api/atletas/{id}
     * Atualiza perfil. Apenas o próprio atleta pode atualizar (valida pelo JWT).
     * Campos omitidos não são alterados.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable UUID id) {
        atletaService.deletar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Conta desativada com sucesso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AtletaDTO>> atualizar(
            @PathVariable UUID id,
            @RequestBody AtletaDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(atletaService.atualizar(id, dto)));
    }

    /**
     * POST /api/atletas/{id}/foto
     * Upload de foto de perfil (multipart/form-data, campo "file").
     * Aceita JPG e PNG. Máximo 10MB (configurado em application.properties).
     */
    @PostMapping("/{id}/foto")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        String filename = atletaService.salvarFoto(id, file);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("fotoPerfil", filename),
                "Foto enviada com sucesso"));
    }

    /**
     * GET /api/atletas/fotos/{filename}
     * Serve o arquivo de foto. Endpoint público (sem autenticação).
     */
    @GetMapping("/fotos/{filename}")
    public ResponseEntity<Resource> servirFoto(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) return ResponseEntity.notFound().build();

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
