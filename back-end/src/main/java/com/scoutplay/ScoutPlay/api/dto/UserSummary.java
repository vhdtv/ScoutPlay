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
public class UserSummary {
    private String nome;
    private String nomeUsuario;
    private String email;
    private String sobrenome;
    private String fotoPerfil;
    private String tipoConta; // 'ATLETA', 'OLHEIRO', 'RESPONSAVEL'
    private int idade;

}
