package com.scoutplay.ScoutPlay.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AtletaDTO {
    private String nome;
    private String telefone;
    private String cpf;
    private String dataNascimento;
    private String cep;
    private String email;
    private String senha;
    private Double peso;
    private Double altura;
    private String posicao;
    private String clubesAnteriores;
    private String peDominante;
    private List<String> videos;
}
