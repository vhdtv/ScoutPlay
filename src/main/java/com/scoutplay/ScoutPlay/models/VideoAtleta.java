package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VideoAtleta {

    @Id
    private String idVideo;
    private String urlVideo;

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    private Atleta atleta;

}
