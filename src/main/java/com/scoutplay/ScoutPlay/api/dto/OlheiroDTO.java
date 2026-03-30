package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para Olheiro
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OlheiroDTO {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private LocalDate dataNascimento;
    private String cep;
    private String clube; // clube que representa
}
