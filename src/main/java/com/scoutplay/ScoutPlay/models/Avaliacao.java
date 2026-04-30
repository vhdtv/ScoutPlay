package com.scoutplay.ScoutPlay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Avaliacao {

    @Id
    @GeneratedValue
    private UUID id;

    private Double nota;
    private String comentario;
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    @JsonIgnore
    private Atleta atleta;

    @ManyToOne
    @JoinColumn(name = "olheiro_id")
    private Olheiro olheiro;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoAtleta video;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}