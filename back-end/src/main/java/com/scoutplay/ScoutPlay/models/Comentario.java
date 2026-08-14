package com.scoutplay.ScoutPlay.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="t_comentario", indexes = {
    @Index(name = "idx_comentario_post_parent", columnList = "fk_post, fk_parent_comentario")
})
@Getter
@Setter
public class Comentario extends TabelaBase {

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "fk_post")
    private Post post;

    private String texto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_parent_comentario")
    private Comentario parentComentario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "t_comentario_curtidas",
        joinColumns = @JoinColumn(name = "fk_comentario"),
        inverseJoinColumns = @JoinColumn(name = "fk_usuario")
    )
    private Set<Usuario> curtidas = new HashSet<>();

    public Comentario() {}

    public Comentario(String texto, Post post, Usuario autor) {
        this.texto = texto;
        this.post = post;
        this.autor = autor;
    }

    public Comentario(String texto, Post post, Usuario autor, Comentario parent) {
        this.texto = texto;
        this.post = post;
        this.autor = autor;
        this.parentComentario = parent;
    }
}
