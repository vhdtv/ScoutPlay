package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="t_detalhes_perfil")
@Getter
@Setter
public class DetalhePerfil extends TabelaBase {
    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private Map<String, Object> data;

    public DetalhePerfil() {}
    public DetalhePerfil(Map<String, Object> _data, Usuario _usuario) {
        this.data = _data;
        this.usuario = _usuario;
    }
}