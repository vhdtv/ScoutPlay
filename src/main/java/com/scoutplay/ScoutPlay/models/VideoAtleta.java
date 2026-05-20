package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_video_atleta")
@Getter
@Setter
public class VideoAtleta extends TabelaBase {

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String urlVideo;

    @Column(nullable = false)
    private String titulo;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void preencherDataCriacao() {
        this.dataCriacao = LocalDateTime.now();
    }

    public VideoAtleta() {}

    public VideoAtleta(Usuario usuario, String urlVideo, String titulo) {
        this.usuario = usuario;
        this.urlVideo = urlVideo;
        this.titulo = titulo;
    }
}
