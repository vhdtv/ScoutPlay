package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="t_avaliacao")
public class Avaliacao extends TabelaBase {
    @JoinColumn(name = "fk_usuario")
    private Usuario criadoPor;
    private String texto;

    public Avaliacao() {}
}