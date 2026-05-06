package com.scoutplay.ScoutPlay.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.Setter;

@Entity
@Table(name="e_tipo_conta")
@Getter
@Setter
public class TipoConta {
    @Id
    @Setter(AccessLevel.NONE)
    private int id;
    @Setter(AccessLevel.NONE)
    private boolean ativo = true;
    private String nome;
    public static final int OLHEIRO = 1;
    public static final int ATLETA = 2;
    public static final int RESPONSAVEL = 3;
    public static final int REPRESENTANTE_CLUBE = 4;

    public TipoConta() {}
    public TipoConta(int _id, String _nome) {
        this.nome = _nome;
        this.id = _id;
    }
}