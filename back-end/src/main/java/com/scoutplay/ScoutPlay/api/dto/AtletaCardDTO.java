package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtletaCardDTO {
    private String username;
    private String nome;
    private String sobrenome;
    private String iniciais;
    private String fotoPerfil;
    private Integer idade;
    private String posicao;
    private String peDominante;
    private String clubesAnteriores;
    private long seguidores;
}
