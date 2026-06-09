package com.scoutplay.ScoutPlay.api.dto;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PostDTO {
    protected UUID url;
    protected String titulo;
    protected Optional<String> descricao;
    protected Optional<Interacoes> interacoes;
    protected PostMediaData media;
    protected LocalDate criadoEm;
}

@Getter
@Setter
class Interacoes {
    protected Boolean deuLike;
    protected Integer quantidadeLike;
    protected ArrayList<PostHighlightDTO> destaques;
}