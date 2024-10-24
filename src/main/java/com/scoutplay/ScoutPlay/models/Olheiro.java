package com.scoutplay.ScoutPlay.models;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Olheiro extends Usuario {

    @Override
    public String gerarIdPersonalizado(){
        return "OLH-" + UUID.randomUUID().toString();
    }
}
