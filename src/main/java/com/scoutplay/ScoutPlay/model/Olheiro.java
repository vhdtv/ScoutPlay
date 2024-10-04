package com.scoutplay.ScoutPlay.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Entity
@Data
public class Olheiro extends Usuario {

    @Override
    protected String gerarIdPersonalizado(){
        return "OLH-" + UUID.randomUUID().toString();
    }
}
