package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @JsonManagedReference
    private Set<DetalhePerfil> detalhePerfil = new HashSet<>();
    @ManyToMany
    @JoinTable(
      name = "t_like",
      joinColumns = @JoinColumn(name = "fk_usuario"),
      inverseJoinColumns = @JoinColumn(name = "fk_post")
    )
    private Map<UUID, Post> postsCurtidos = new HashMap<UUID, Post>();
    @ManyToMany
    @JoinTable(
      name = "t_contas_que_segue",
      joinColumns = @JoinColumn(name = "fk_usuario"),
      inverseJoinColumns = @JoinColumn(name = "fk_seguindo")
    )
    private Set<Usuario> contasQueSegue = new HashSet<>();
    @ManyToMany(mappedBy = "contasQueSegue")
    private Set<Usuario> contasQueMeSeguem = new HashSet<>();
    @ManyToMany
    @JoinTable(
      name = "t_tipo_conta",
      joinColumns = @JoinColumn(name = "fk_usuario"),
      inverseJoinColumns = @JoinColumn(name = "fk_tipo_conta")
    )
    private Set<TipoConta> poderesConta = new HashSet<>();

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

    public void curtirPost(Post post) {
        Boolean jaDeuLike = this.postsCurtidos.get(post.getAliasId()) != null;
        if (jaDeuLike) return;
        this.postsCurtidos.put(post.getAliasId(), post);
        post.getUsuariosQueCurtiram().add(this);
    }

    public void descurtirPost(Post post) {
        Boolean naoDeuLike = this.postsCurtidos.get(post.getAliasId()) == null;
        if (naoDeuLike) return;
        this.postsCurtidos.remove(post.getAliasId());
        post.getUsuariosQueCurtiram().remove(this);
    }

    public void seguir(Usuario conta) {
        if(this.contasQueSegue.contains(conta)) throw new Error("Já segue esta conta");
        this.contasQueSegue.add(conta);
        conta.contasQueMeSeguem.add(this);
    }

    public void pararDeSeguir(Usuario conta) {
        if(!this.contasQueSegue.contains(conta)) throw new Error("Já não segue esta conta");
        this.contasQueSegue.remove(conta);
        conta.contasQueMeSeguem.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario that = (Usuario) o;
        return this.getAliasId().equals(that.getAliasId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
