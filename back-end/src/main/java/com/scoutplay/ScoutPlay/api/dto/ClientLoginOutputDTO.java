package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginOutputDTO {
    private String tokenAcesso;
    private Long expiraEm;
    UserSummaryDTO usuario;

}
