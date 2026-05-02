package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

@Data
@Table(name="t_usuario")
public class Usuario extends TabelaBase {
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String url;
    private String nome;
    private String sobrenome;
    private String enderecoUnico; // é o arroba dele
    private String senha;
    private String telefone;
    private String fotoPerfil;

    protected Usuario() {}
    public Usuario(String _nome, String _email, String _senha, LocalDate _dataNascimento) {
        this.nome = _nome;
        this.email = _email;
        this.senha = _senha; // crypto this before (SHA 256)
        this.dataNascimento = _dataNascimento;
        this.gerarEnderecoUnico();
    }

    @Override
    public String toString() {
        return String.format("%s(@%s) - %d anos", this.nome, this.enderecoUnico, this.obterIdade());
    }

    public Integer obterIdade() {
        if(this.dataNascimento == null) return null;
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }

    private void gerarEnderecoUnico() {
        String enderecoGerado = "";
        boolean enderecoGeradoJaExisteNoBanco = true;
        do {
            enderecoGerado = this.nome.replaceAll(" ", "_");
            Random rand = new Random();
            final int MIN_VALUE = 1000;
            final int MAX_VALUE = 99999;
            enderecoGerado += rand.nextInt(MAX_VALUE - MIN_VALUE) + MIN_VALUE;
            // find on db
        } while(enderecoGeradoJaExisteNoBanco);
        
        this.enderecoUnico = enderecoGerado;
    }

}
