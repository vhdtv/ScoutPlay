package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
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
    private List<VideoAtleta> videos;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavel;

    @Override
    public String gerarIdPersonalizado(){
        return "ATL-" + UUID.randomUUID().toString();
    }


}
enum PeDominante {
    DESTRO, CANHOTO, AMBIDESTRO
}