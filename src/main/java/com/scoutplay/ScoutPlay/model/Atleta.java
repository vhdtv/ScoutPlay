package com.scoutplay.ScoutPlay.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Data
public class Atleta extends Usuario {
    private String posicao;
    private Double peso;
    private Double altura;
    private String clubesAnteriores;
    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    private PeDominante peDominante;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL)
    private ArrayList<VideoAtleta> videos;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavel;

    @Override
    protected String gerarIdPersonalizado(){
        return "ATL-" + UUID.randomUUID().toString();
    }


}
enum PeDominante {
    DESTRO, CANHOTO, AMBIDESTRO
}