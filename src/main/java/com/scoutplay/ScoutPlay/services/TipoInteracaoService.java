package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.TipoInteracao;
import com.scoutplay.ScoutPlay.repositories.TipoInteracaoRepository;
import jakarta.transaction.Transactional;

@Service
public class TipoInteracaoService {
    @Autowired
    TipoInteracaoRepository tipoInteracaoRepository;

    @Transactional
    public void injetarValores() {
        if(tipoInteracaoRepository.count() != 0) return;
        
        ArrayList<TipoInteracao> tipos = new ArrayList<>();
        tipos.add(new TipoInteracao(1, "like"));
        tipos.add(new TipoInteracao(2, "comentario"));
        tipos.add(new TipoInteracao(3, "seguiu"));
        tipos.add(new TipoInteracao(4, "avaliou"));
        tipoInteracaoRepository.saveAllAndFlush(tipos);
    }
}