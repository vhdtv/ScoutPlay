package com.scoutplay.ScoutPlay.api.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PostHighlightDTO {
    private UUID aliasId;
    private String nome;
    private int count;
    private boolean marcadoPeloUsuario;
}