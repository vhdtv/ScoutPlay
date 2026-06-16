package com.scoutplay.ScoutPlay.models;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class TabelaBase {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Getter
    @Setter
    private java.util.UUID aliasId;
    @Getter
    @Setter
    private boolean ativo;
    @Getter
    private LocalDateTime criadoEm;
    @Getter
    @Setter
    private LocalDateTime atualizadoEm;


    @PrePersist
    public void __beforeCreation() {
        if(this.aliasId == null) this.aliasId = java.util.UUID.randomUUID();
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
        this.ativo = true;
    }
}
