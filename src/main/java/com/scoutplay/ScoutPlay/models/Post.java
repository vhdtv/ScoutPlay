package com.scoutplay.ScoutPlay.models;

import com.scoutplay.ScoutPlay.enums.TipoMidia;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="t_post")
public class Post extends TabelaBase {
    private String titulo;
    private String descricao;
    private TipoMidia tipoMidia;
    private String caminhoArquivo;

    public Post() {}

}
