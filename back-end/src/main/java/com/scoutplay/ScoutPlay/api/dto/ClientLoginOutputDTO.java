package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginOutputDTO {
    @NonNull private String tokenAcesso;
    private Long expiraEm;
    UserSummaryDTO usuario;

}
