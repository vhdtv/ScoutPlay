package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class VideoAtleta {

    @Id
    private UUID idVideo;
    private String urlVideo;

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    private Atleta atleta;

}
