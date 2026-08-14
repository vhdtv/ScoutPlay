package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import lombok.Getter;

@Entity
@Table(name="xref_usuario_tipoconta", uniqueConstraints =
    @UniqueConstraint(name = "uk_usuario_tipo_conta", columnNames = {"fk_usuario", "fk_tipo_conta"}))
public class XUsuarioTipoConta extends TabelaBase {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_usuario")
    @Getter
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
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
