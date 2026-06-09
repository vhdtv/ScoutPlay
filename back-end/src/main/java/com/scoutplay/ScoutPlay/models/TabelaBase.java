package com.scoutplay.ScoutPlay.models;

import java.time.LocalDate;

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
    private LocalDate criadoEm;
    @Getter
    @Setter
    private LocalDate atualizadoEm;
    

    @PrePersist
    public void __beforeCreation() {
        if(this.aliasId == null) this.aliasId = java.util.UUID.randomUUID();
        this.criadoEm = LocalDate.now();
        this.atualizadoEm = this.criadoEm;
        this.ativo = true;
    }
}