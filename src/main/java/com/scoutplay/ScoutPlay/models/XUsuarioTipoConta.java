package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name="xref_usuario_tipoconta")
public class XUsuarioTipoConta extends TabelaBase {
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_usuario")
    @Getter
    private Usuario usuario;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_tipo_conta")
    @Getter
    private TipoConta tipoConta;

    public XUsuarioTipoConta() {}
    public XUsuarioTipoConta(Usuario _usuario, TipoConta _tipoConta) {
        this.usuario = _usuario;
        this.tipoConta = _tipoConta;
    }

    public String toString() {
        String tipoConta = "Atleta";
        switch(this.tipoConta.getId()) {
            case TipoConta.OLHEIRO:
                tipoConta = "Olheiro";
                break;
            case TipoConta.REPRESENTANTE_CLUBE:
                tipoConta = "Representante de Clube";
                break;
            case TipoConta.RESPONSAVEL:
                tipoConta = "Responsavel";
                break;
        }
        return String.format("XUsuarioTipoConta: %s, %s", this.usuario, tipoConta);
    }
}