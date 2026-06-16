package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.enums.TipoContaEnum;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.TipoContaRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class TipoContaService {
    @Autowired
    TipoContaRepository tipoContaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public void injetarValores() {
        if(tipoContaRepository.count() != 0) return;
        
        ArrayList<TipoConta> tipos = new ArrayList<>();
        tipos.add(new TipoConta(TipoContaEnum.ATLETA));
        tipos.add(new TipoConta(TipoContaEnum.OLHEIRO));
        tipos.add(new TipoConta(TipoContaEnum.RESPONSAVEL));
        tipos.add(new TipoConta(TipoContaEnum.REPRESENTANTE_CLUBE));
        tipoContaRepository.saveAllAndFlush(tipos);
    }

    @Transactional
    public boolean verificarTipoConta(Usuario usuario, TipoContaEnum poderAProcurar) {
        return usuario.getPoderesConta().stream().anyMatch(poder -> poder.getId() == poderAProcurar.getId());
    }
    
    @Transactional
    public void categorizarContaComoAtleta(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId).get();
        usuario.getPoderesConta().add(new TipoConta(TipoContaEnum.ATLETA));
    }
    
    @Transactional
    public void categorizarContaComoResponsavel(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId).get();
        usuario.getPoderesConta().add(new TipoConta(TipoContaEnum.RESPONSAVEL));
    }
    
    @Transactional
    public void categorizarContaComoOlheiro(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId).get();
        usuario.getPoderesConta().add(new TipoConta(TipoContaEnum.OLHEIRO));
    }
}