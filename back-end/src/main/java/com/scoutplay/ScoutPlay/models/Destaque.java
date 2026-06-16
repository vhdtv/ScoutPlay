package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_destaque")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Destaque extends TabelaBase {

    @Column(nullable = false, unique = true)
    private String nome;
}