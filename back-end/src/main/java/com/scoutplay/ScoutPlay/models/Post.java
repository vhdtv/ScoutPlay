package com.scoutplay.ScoutPlay.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name="t_post")
public class Post extends TabelaBase {
    private String titulo;
    private String descricao;
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoMidia tipoMidia;
    private String caminhoArquivo;
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario autor;
    @ManyToMany(mappedBy = "postsCurtidos") 
    @ToString.Exclude
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
        return switch(this.obterExtensaoDaMidia().toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png"         -> "image/png";
            case "webp"        -> "image/webp";
            case "mp4"         -> "video/mp4";
            case "mov"         -> "video/quicktime";
            default            -> "image/jpeg";
        };
    }
}
