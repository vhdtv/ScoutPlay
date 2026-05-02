package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="t_comentario")
public class Comentario extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario autor;
    @ManyToOne
    @JoinColumn(name = "fk_post")
    private Post post;
    private String texto;
    
    public Comentario() {}
}