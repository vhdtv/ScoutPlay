package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.TipoMidia;
import com.scoutplay.ScoutPlay.repositories.TipoMidiaRepository;

import jakarta.transaction.Transactional;

@Service
public class TipoMidiaService {
    @Autowired
    TipoMidiaRepository tipoMidiaRepository;

    @Transactional
    public void injetarValores() {
        if(tipoMidiaRepository.count() != 0) return;
        
        ArrayList<TipoMidia> tipos = new ArrayList<>();
        tipos.add(new TipoMidia(1, "foto"));
        tipos.add(new TipoMidia(2, "video"));
        tipoMidiaRepository.saveAllAndFlush(tipos);
    }
}