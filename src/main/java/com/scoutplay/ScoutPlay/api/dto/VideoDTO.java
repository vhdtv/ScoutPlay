package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para Video
 * Representa um vídeo de um atleta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private UUID id;

    @NotBlank(message = "URL do vídeo é obrigatória")
    private String urlVideo;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
    private String titulo;

    @NotNull(message = "Data de criação é obrigatória")
    private LocalDateTime dataCriacao;
}
