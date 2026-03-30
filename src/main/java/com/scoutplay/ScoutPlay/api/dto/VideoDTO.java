package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String urlVideo;
    private String titulo;
    private LocalDateTime dataCriacao;
}
