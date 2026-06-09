package com.scoutplay.ScoutPlay.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="e_midia")
public class TipoMidia {
    @Id
    @Getter
    private int id;
    @Getter
    @Setter
    private boolean ativo;
    @Getter
    @Setter
    private String nome;

    public TipoMidia() {}
    public TipoMidia(int _id, String _nome) {
        this.id = _id;
        this.nome = _nome;
    }

    public String toString() {
        return this.nome;
    }
}