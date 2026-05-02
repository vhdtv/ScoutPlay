package com.scoutplay.ScoutPlay.models;

import java.time.LocalDate;

import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class TabelaBase {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String id;
    @UUID
    private java.util.UUID aliasId;
    private boolean ativo;
    private LocalDate criadoEm;
    private LocalDate atualizadoEm;
    

    @PrePersist
    public void __beforeCreation() {
        this.aliasId = java.util.UUID.randomUUID();
        this.criadoEm = LocalDate.now();
        this.atualizadoEm = this.criadoEm;
        this.ativo = true;
    }
}