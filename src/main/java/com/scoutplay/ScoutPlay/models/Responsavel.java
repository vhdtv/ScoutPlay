package com.scoutplay.ScoutPlay.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Responsavel extends Usuario{
    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Atleta> atletas;

    @Override
    protected String gerarIdPersonalizado(){
        return "RESP-" + UUID.randomUUID().toString();
    }
}
