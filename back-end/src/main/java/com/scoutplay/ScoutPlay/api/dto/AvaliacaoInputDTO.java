package com.scoutplay.ScoutPlay.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvaliacaoInputDTO {
    @NotNull
    @Min(1) @Max(10)
    private Double nota;
    private String comentario;
}
