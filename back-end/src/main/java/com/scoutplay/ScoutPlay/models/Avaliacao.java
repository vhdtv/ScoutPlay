package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_avaliacao")
@Getter
@Setter
public class Avaliacao extends TabelaBase {

    @ManyToOne
    @JoinColumn(name = "fk_atleta", nullable = false)
    private Usuario atleta;

    @ManyToOne
    @JoinColumn(name = "fk_olheiro", nullable = false)
    private Usuario olheiro;

    @ManyToOne
    @JoinColumn(name = "fk_video")
    private VideoAtleta video;

    @Column(nullable = false)
    private Double nota;

    @Column(length = 500)
    private String comentario;

    public Avaliacao() {}

    public Avaliacao(Usuario atleta, Usuario olheiro, Double nota, String comentario, VideoAtleta video) {
        this.atleta = atleta;
        this.olheiro = olheiro;
        this.nota = nota;
        this.comentario = comentario;
        this.video = video;
    }
}