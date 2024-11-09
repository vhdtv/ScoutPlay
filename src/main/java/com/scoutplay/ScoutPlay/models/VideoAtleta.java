package com.scoutplay.ScoutPlay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class VideoAtleta {

    @Id
    @GeneratedValue
    private UUID idVideo;
    private String urlVideo;

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    @JsonIgnore // Ignora o campo atleta na serialização evitando um loop
    private Atleta atleta;

}
