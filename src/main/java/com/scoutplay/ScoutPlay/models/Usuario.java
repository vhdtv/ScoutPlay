package com.scoutplay.ScoutPlay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@MappedSuperclass
@Data

public abstract class Usuario {
    @Id
    private String id;

    private String nome;
    private String telefone;
    private String cpf;
    private LocalDate dataNascimento;
    private String cep;
    private String email;
    @JsonIgnore
    private String senha;

    public Usuario(){
        this.id = gerarIdPersonalizado();
    }

    @Transient
    public Integer getIdade() {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    protected abstract String gerarIdPersonalizado();

}
