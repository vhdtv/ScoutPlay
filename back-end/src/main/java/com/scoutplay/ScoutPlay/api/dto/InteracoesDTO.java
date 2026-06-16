package com.scoutplay.ScoutPlay.api.dto;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteracoesDTO {
    protected Boolean deuLike;
    protected Integer quantidadeLike;
    protected Integer quantidadeComentarios;
    protected ArrayList<PostHighlightDTO> destaques;
}