package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="t_usuario")
@Getter
@Setter    
public class Usuario extends TabelaBase {
    @Setter(AccessLevel.NONE)
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String nome;
    private String sobrenome;
    private String username;
    private String senha;
    private String telefone;
    private String fotoPerfil;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private List<DetalhePerfil> detalhePerfil = new ArrayList<>();

    protected Usuario() {}
    public Usuario(String _nome, String _email, String _senha, LocalDate _dataNascimento) {
        this.setNome(_nome);
        this.setEmail(_email);
        this.setSenha(_senha);
        this.setDataNascimento(_dataNascimento);
    }
    public Usuario(String _nome, String _sobrenome, String _email, String _senha, LocalDate _dataNascimento) {
        this.setNome(_nome);
        this.setSobrenome(_sobrenome);
        this.setEmail(_email);
        this.setSenha(_senha);
        this.setDataNascimento(_dataNascimento);
    }
    public Usuario(String _nome, String _sobrenome, String _email, String _cpf, String _senha, LocalDate _dataNascimento) {
        this.setNome(_nome);
        this.setSobrenome(_sobrenome);
        this.setEmail(_email);
        this.setSenha(_senha);
        this.setCpf(_cpf);
        this.setDataNascimento(_dataNascimento);
    }
    
    @Override
    public String toString() {
        return String.format("%s(@%s) - %d anos", this.nome, this.username, this.obterIdade());
    }
    
    public void setCpf(String _cpf) {
        System.out.println("123123123");
        if(this.cpf != null) return;
        this.cpf = _cpf;
    }

    public Integer obterIdade() {
        if(this.dataNascimento == null) return null;
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }
    public String getNomeCompleto() {
        return this.getNome() + " " + this.getSobrenome();
    }
    public String getIniciais() {
        return this.nome.charAt(0) + "" + this.sobrenome.charAt(0);
    }

}
