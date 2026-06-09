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
public class PostAuthorSummary {
    private String nome;
    private String sobrenome;
    private String iniciais;
    private String username;
    private String fotoPerfil;
}
