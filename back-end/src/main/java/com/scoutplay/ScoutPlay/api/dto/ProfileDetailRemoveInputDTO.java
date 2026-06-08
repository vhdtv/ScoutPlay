package com.scoutplay.ScoutPlay.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailRemoveInputDTO {
    @NotBlank(message = "Informe o campo")
    @JsonProperty("chave")
    private String chave;

}
