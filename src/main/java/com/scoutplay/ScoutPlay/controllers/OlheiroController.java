package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.OlheiroDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.services.OlheiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/olheiros")
@RequiredArgsConstructor
public class OlheiroController {

    private final OlheiroService olheiroService;

    /**
     * POST /api/olheiros/registro
     * Cria conta de olheiro e retorna token JWT imediatamente.
     */
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrar(@Valid @RequestBody OlheiroDTO dto) {
        LoginResponse response = olheiroService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Olheiro registrado com sucesso"));
    }

    /**
     * GET /api/olheiros?page=0&size=10
     * Lista olheiros paginado. Requer autenticação.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OlheiroDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(olheiroService.listar(page, size)));
    }

    /**
     * GET /api/olheiros/{id}
     * Perfil completo do olheiro. O {id} é o aliasId (UUID).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OlheiroDTO>> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(olheiroService.buscar(id)));
    }

    /**
     * PUT /api/olheiros/{id}
     * Atualiza perfil. Apenas o próprio olheiro pode atualizar.
     * Campos omitidos não são alterados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OlheiroDTO>> atualizar(
            @PathVariable UUID id,
            @RequestBody OlheiroDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(olheiroService.atualizar(id, dto)));
    }
}
