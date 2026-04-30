package com.scoutplay.ScoutPlay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class VideoAtleta {

    @Id
    @GeneratedValue
    private UUID idVideo;
    private String urlVideo;
    private String titulo;
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    @JsonIgnore // Ignora o campo atleta na serialização evitando um loop
    private Atleta atleta;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

}
