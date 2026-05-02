package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.scoutplay.ScoutPlay.enums.TipoDetalhePerfil;

@Entity
@Table(name="t_detalhes_usuario")
public class DetalhesUsuario extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
    private TipoDetalhePerfil detalhe;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private Map<String, Object> data;

    public DetalhesUsuario() {}
}