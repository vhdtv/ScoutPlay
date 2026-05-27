package com.scoutplay.ScoutPlay.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {

    // Obrigatório apenas ao criar (POST). Ignorado na resposta.
    @NotNull(message = "ID do atleta é obrigatório")
    private UUID atletaId;

    // Preenchido na resposta (GET)
    private UUID id;
    private UUID olheiroId;
    private String nomeOlheiro;
    private String nomeAtleta;

    @NotNull(message = "Nota é obrigatória")
    @Min(value = 0, message = "Nota mínima é 0")
    @Max(value = 10, message = "Nota máxima é 10")
    private Double nota;

    @Size(max = 500, message = "Comentário deve ter no máximo 500 caracteres")
    private String comentario;

    // Opcional: vincular avaliação a um vídeo específico
    private UUID videoId;
}