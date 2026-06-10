package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="t_comentario")
@Getter
@Setter
public class Comentario extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    @Getter
    private Usuario autor;
    @ManyToOne
    @JoinColumn(name = "fk_post")
    @Getter
    private Post post;
    @Getter
    @Setter
    private String texto;
    
    public Comentario() {}
    public Comentario(String texto, Post post, Usuario autor) {
        this.texto = texto;
        this.post = post;
        this.autor = autor;
    }
}