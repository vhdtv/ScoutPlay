package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="t_post")
public class Post extends TabelaBase {
    @Getter
    @Setter
    private String titulo;
    @Getter
    @Setter
    private String descricao;
    @Getter
    @Setter
    @ManyToOne
    private TipoMidia tipoMidia;
    @Getter
    @Setter
    private String caminhoArquivo;

    public Post() {}

}
