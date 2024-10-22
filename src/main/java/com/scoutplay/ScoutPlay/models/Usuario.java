package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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
    private String senha;

    public Usuario(){
        this.id = gerarIdPersonalizado();
    }
    protected abstract String gerarIdPersonalizado();

}
