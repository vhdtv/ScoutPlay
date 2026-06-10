package com.scoutplay.ScoutPlay.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="t_post")
public class Post extends TabelaBase {
    private String titulo;
    private String descricao;
    @ManyToOne 
    private TipoMidia tipoMidia;
    private String caminhoArquivo;
    @ManyToOne 
    private Usuario autor;
    @ManyToMany(mappedBy = "postsCurtidos") 
    private Set<Usuario> usuariosQueCurtiram = new HashSet<Usuario>();

    public Post() {}
    public Post(String titulo, String descricao, String caminhoArquivo, TipoMidia tipoMidia, Usuario autor) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.caminhoArquivo = caminhoArquivo;
        this.tipoMidia = tipoMidia;
        this.autor = autor;
    }
    public String obterExtensaoDaMidia() {
        if (caminhoArquivo == null || !caminhoArquivo.contains(".")) return "";
        return caminhoArquivo.substring(caminhoArquivo.lastIndexOf(".") + 1);
    }
    public String obterMimeType() {
        switch(this.obterExtensaoDaMidia()) {
            case "jpg":
                return "image/jpg";
            case "mp4":
                return "video/mp4";
            default: 
                return "";
        }
    }
}
