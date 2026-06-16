package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_seguidor")
@Getter
@Setter
@NoArgsConstructor
public class Seguidor extends TabelaBase {

    @ManyToOne
    @JoinColumn(name = "fk_seguidor")
    private Usuario seguidor;

    @ManyToOne
    @JoinColumn(name = "fk_seguido")
    private Usuario seguido;

    public Seguidor(Usuario seguidor, Usuario seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }
}
