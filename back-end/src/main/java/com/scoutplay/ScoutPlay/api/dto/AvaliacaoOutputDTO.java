package com.scoutplay.ScoutPlay.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoOutputDTO {
    private UUID id;
    private Double nota;
    private String comentario;
    private UserSummaryDTO olheiro;
    private LocalDateTime criadoEm;
}
