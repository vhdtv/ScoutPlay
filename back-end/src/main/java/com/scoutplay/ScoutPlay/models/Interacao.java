package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="t_interacao")
public class Interacao extends TabelaBase {
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "fk_post")
    private Post post;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private Map<String, Object> data;

    public Interacao() {}
}