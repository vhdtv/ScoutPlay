package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.services.AIService;
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
public class AIController {

    private final AIService aiService;

    /**
     * POST /api/ia/prompt
     * Corpo esperado (JSON): { "pergunta": "Quais são os atacantes?" }
     */
    @PostMapping("/prompt")
    public ResponseEntity<ApiResponse<Map<String, String>>> perguntar(
            @RequestBody Map<String, String> body) {

        String pergunta = body.get("pergunta");
        if (pergunta == null || pergunta.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", "O campo 'pergunta' é obrigatório."));
        }

        String resposta = aiService.perguntar(pergunta);

        return ResponseEntity.ok(ApiResponse.success(Map.of("resposta", resposta)));
    }
}