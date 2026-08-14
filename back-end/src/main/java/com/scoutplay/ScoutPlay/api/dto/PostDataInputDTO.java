package com.scoutplay.ScoutPlay.api.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDataInputDTO {
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 120, message = "Título deve ter no máximo 120 caracteres")
    private String titulo;
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    @NotNull(message = "Arquivo é obrigatório")
    private MultipartFile arquivo;
}
