package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.TipoDetalhePerfil;
import com.scoutplay.ScoutPlay.repositories.TipoDetalhePerfilRepository;
import jakarta.transaction.Transactional;

@Service
public class TipoDetalhePerfilService {
    @Autowired
    TipoDetalhePerfilRepository tipoDetalhePerfilRepository;

    @Transactional
    public void injetarValores() {
        if(tipoDetalhePerfilRepository.count() != 0) return;
        
        ArrayList<TipoDetalhePerfil> tipos = new ArrayList<>();
        tipos.add(new TipoDetalhePerfil(1, "pe_dominante"));
        tipos.add(new TipoDetalhePerfil(2, "clube_que_participou"));
        tipos.add(new TipoDetalhePerfil(3, "video_apresentacao"));
        tipos.add(new TipoDetalhePerfil(4, "altura"));
        tipos.add(new TipoDetalhePerfil(5, "configuracao_de_perfil"));
        tipos.add(new TipoDetalhePerfil(6, "posicao"));
        tipos.add(new TipoDetalhePerfil(7, "responsavel"));
        tipoDetalhePerfilRepository.saveAllAndFlush(tipos);
    }
}