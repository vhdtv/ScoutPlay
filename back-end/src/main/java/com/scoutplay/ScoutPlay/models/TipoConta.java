package com.scoutplay.ScoutPlay.models;
import java.util.Objects;

import com.scoutplay.ScoutPlay.enums.TipoContaEnum;

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

    public TipoConta() {}
    public TipoConta(TipoContaEnum tipo) {
        this.id = tipo.getId();
        this.nome = tipo.getNome();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoConta that = (TipoConta) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}