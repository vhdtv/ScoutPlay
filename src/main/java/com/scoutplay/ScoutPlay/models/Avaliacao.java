package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="t_avaliacao")
public class Avaliacao extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario criadoPor;
    @Getter
    @Setter
    private String texto;

    public Avaliacao() {}
}