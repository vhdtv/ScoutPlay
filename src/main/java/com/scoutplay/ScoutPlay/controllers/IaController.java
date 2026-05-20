package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.services.IaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IaController {

    private final IaService iaService;

    /**
     * POST /api/ia/copiloto
     * Copiloto ScoutPlay — responde perguntas sobre futebol e atletas cadastrados.
     * Requer token JWT. Body: { "pergunta": "..." }
     */
    @PostMapping("/copiloto")
    public ResponseEntity<ApiResponse<Map<String, String>>> perguntar(
            @RequestBody Map<String, String> body) {

        String pergunta = body.get("pergunta");
        if (pergunta == null || pergunta.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", "Campo 'pergunta' é obrigatório"));
        }

        String resposta = iaService.perguntar(pergunta);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resposta", resposta)));
    }
}
