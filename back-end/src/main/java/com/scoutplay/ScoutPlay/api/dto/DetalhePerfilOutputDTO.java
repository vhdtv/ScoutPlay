package com.scoutplay.ScoutPlay.api.dto;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login
 * Retorna o token JWT e informações do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalhePerfilOutputDTO {
    private UUID id;
    private Map<String, Object> data;
    private UUID userId;
}
