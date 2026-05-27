package com.scoutplay.ScoutPlay.api.dto;

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
public class LoginResponse {
    private String token;
    private String userId;
    private String userType; // 'ATLETA', 'OLHEIRO', 'RESPONSAVEL'
    private String nome;
    private String email;
    private Long expiresIn;   // Tempo de expiração em milissegundos
}
