package com.scoutplay.ScoutPlay.api.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSummary {
    private String nome;
    private String sobrenome;
    private String iniciais;
    private String username;
    private String fotoPerfil;
    private String[] tipoConta;
    private String cidade;
    private Integer idade;
    private Map<String, Object> detalhesPerfil;
    private List<PostResume> posts;
    private long seguidores;
    private long seguindo;
    private boolean souSeguidor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResume {
        private String url;
        private String titulo;
        private String src;
        private String mimeType;
    }
}
