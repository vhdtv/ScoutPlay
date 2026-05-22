package com.scoutplay.ScoutPlay.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private UUID id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 120, message = "Título deve ter no máximo 120 caracteres")
    private String titulo;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    private String caminhoArquivo;
    private String tipoMidia;

    // campos de resposta (somente leitura)
    private UUID autorId;
    private String autorNome;
    private LocalDate criadoEm;
}
