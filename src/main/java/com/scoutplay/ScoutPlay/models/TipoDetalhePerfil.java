package com.scoutplay.ScoutPlay.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="e_detalhe_perfil")
public class TipoDetalhePerfil {
    @Id
    @Getter
    private int id;
    @Getter
    @Setter
    private boolean ativo;
    @Getter
    private String nome;

    public TipoDetalhePerfil() {}
    public TipoDetalhePerfil(int _id, String _nome) {
        this.id = _id;
        this.nome = _nome;
    }
}