package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="t_usuario", uniqueConstraints = {
    @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_usuario_username", columnNames = "username"),
    @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf")
})
@Getter
@Setter    
public class Usuario extends TabelaBase {
    @Setter(AccessLevel.NONE)
    private String cpf;
    private LocalDate dataNascimento;
    @Column(nullable = false, length = 254)
    private String email;
    private String nome;
    private String sobrenome;
    @Column(nullable = false, length = 80)
    private String username;
    private String senha;
    private String telefone;
    private String fotoPerfil;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    @JsonManagedReference
    private List<DetalhePerfil> detalhePerfil = new ArrayList<>();
    @ManyToMany
    @JoinTable(
      name = "t_like",
      joinColumns = @JoinColumn(name = "fk_usuario"),
      inverseJoinColumns = @JoinColumn(name = "fk_post")
    )
    private Set<Post> postsCurtidos = new HashSet<>();

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
        if(this.cpf != null) return;
        this.cpf = _cpf;
    }

    public Integer obterIdade() {
        if(this.dataNascimento == null) return null;
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }
    
    public String getNomeCompleto() {
        return (this.getNome() + " " + (this.getSobrenome() == null ? "" : this.getSobrenome())).trim();
    }
    
    public String getIniciais() {
        String primeira = nome == null || nome.isBlank() ? "?" : nome.substring(0, 1);
        String segunda = sobrenome == null || sobrenome.isBlank() ? "" : sobrenome.substring(0, 1);
        return (primeira + segunda).toUpperCase();
    }

    public void curtirPost(Post post) {
        if (this.postsCurtidos.contains(post)) return;
        this.postsCurtidos.add(post);
        post.getUsuariosQueCurtiram().add(this);
    }

    public void descurtirPost(Post post) {
        if (!this.postsCurtidos.contains(post)) return;
        this.postsCurtidos.remove(post);
        post.getUsuariosQueCurtiram().remove(this);
    }

}
