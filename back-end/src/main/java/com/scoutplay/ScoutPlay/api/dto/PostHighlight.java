package com.scoutplay.ScoutPlay.api.dto;

import java.util.UUID;

public interface PostHighlight {
    UUID getAliasId();
    String getNome();
    int getQuantidadeMarcada();
    boolean getMarcadoPeloUsuario();
}