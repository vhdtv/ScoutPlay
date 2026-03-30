package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para Atleta
 * Usado para transferir dados do backend para o frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtletaDTO {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private LocalDate dataNascimento;
    private Double altura;
    private Double peso;
    private String posicao;
    private String peDominante;
    private String cep;
    private String fotoPerfil;
    private String clubesAnteriores;
    private Integer idade; // calculado em tempo de resposta
    private List<VideoDTO> videos;
}
