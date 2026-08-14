package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_seguidor", uniqueConstraints =
    @UniqueConstraint(name = "uk_seguidor_relacao", columnNames = {"fk_seguidor", "fk_seguido"}))
@Getter
@Setter
@NoArgsConstructor
public class Seguidor extends TabelaBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_seguidor")
    private Usuario seguidor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_seguido")
    private Usuario seguido;

    public Seguidor(Usuario seguidor, Usuario seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }
}
