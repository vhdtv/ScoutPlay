package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConviteOutputDTO {
    private UserSummaryDTO remetente;
    private UserSummaryDTO destinatario;
    private boolean aceito;
    private String mensagem;
}