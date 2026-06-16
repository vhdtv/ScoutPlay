package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.scoutplay.ScoutPlay.models.TipoMidia;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDataOutputDTO {
    private UUID url;
    private String titulo;
    private Optional<String> descricao;
    private String src;
    private String poster;
    private LocalDateTime criadoEm;
    private TipoMidia tipoMidia;
    private PostAuthorSummary autor;
}
