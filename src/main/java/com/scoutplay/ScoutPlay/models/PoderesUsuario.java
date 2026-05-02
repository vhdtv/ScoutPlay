package com.scoutplay.ScoutPlay.models;

import com.scoutplay.ScoutPlay.enums.TipoConta;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="xref_usuario_tipoconta")
public class PoderesUsuario extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "fk_tipo_conta")
    private TipoConta tipoConta;

    public PoderesUsuario() {}
}