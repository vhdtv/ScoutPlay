package com.scoutplay.ScoutPlay.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDataInputDTO {
    @NotBlank(message = "Comentário não pode estar vazio")
    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    private String texto;
}
