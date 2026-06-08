package com.scoutplay.ScoutPlay.api.dto;

import java.util.Collection;

import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Post;

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
public class UserProfileSummary {
    private String nome;
    private String sobrenome;
    private String username;
    private String fotoPerfil;
    private String[] tipoConta;
    private String cidade;
    private int idade;
    private Collection<DetalhePerfil> detalhes;
    private Collection<Post> posts;
}
