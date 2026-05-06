package com.scoutplay.ScoutPlay.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="e_interacao")
public class TipoInteracao {
    @Id
    @Getter
    private int id;
    @Getter
    @Setter
    private boolean ativo;
    @Getter
    @Setter
    private String nome;

    public TipoInteracao() {}
    public TipoInteracao(int _id, String _nome) {
        this.id = _id;
        this.nome = _nome;
    }
}