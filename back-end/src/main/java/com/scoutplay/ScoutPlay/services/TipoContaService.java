package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.repositories.TipoContaRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;

import jakarta.transaction.Transactional;

@Service
public class TipoContaService {
    @Autowired
    TipoContaRepository tipoContaRepository;
    @Autowired
    XUsuarioTipoContaRepository xUsuarioTipoContaRepository;

    @Transactional
    public void injetarValores() {
        if(tipoContaRepository.count() != 0) return;
        
        ArrayList<TipoConta> tipos = new ArrayList<>();
        tipos.add(new TipoConta(TipoConta.OLHEIRO, "olheiro"));
        tipos.add(new TipoConta(TipoConta.ATLETA, "atleta"));
        tipos.add(new TipoConta(TipoConta.RESPONSAVEL, "responsavel"));
        tipos.add(new TipoConta(TipoConta.REPRESENTANTE_CLUBE, "representante de clube"));
        tipoContaRepository.saveAllAndFlush(tipos);
    }

    @Transactional
    public boolean verificarTipoConta(Usuario usuario, int TIPO_CONTA) {
        Set<XUsuarioTipoConta> tipoConta = xUsuarioTipoContaRepository.getByUsuario(usuario);
        return tipoConta.stream().anyMatch(e -> e.getTipoConta().getId() == TIPO_CONTA);
    }
    
    @Transactional
    public void categorizarContaComoAtleta(Usuario usuario) {
        TipoConta TIPO_ATLETA = this.tipoContaRepository.getByNome("atleta");
        XUsuarioTipoConta relacaoTipoConta = new XUsuarioTipoConta(usuario, TIPO_ATLETA);
        xUsuarioTipoContaRepository.saveAndFlush(relacaoTipoConta);
    }
    
    @Transactional
    public void categorizarContaComoResponsavel(Usuario usuario) {
        TipoConta TIPO_RESPONSAVEL = this.tipoContaRepository.getByNome("responsavel");
        XUsuarioTipoConta relacaoTipoConta = new XUsuarioTipoConta(usuario, TIPO_RESPONSAVEL);
        xUsuarioTipoContaRepository.saveAndFlush(relacaoTipoConta);
    }
    
    @Transactional
    public void categorizarContaComoOlheiro(Usuario usuario) {
        TipoConta TIPO_OLHEIRO = this.tipoContaRepository.getByNome("olheiro");
        XUsuarioTipoConta relacaoTipoConta = new XUsuarioTipoConta(usuario, TIPO_OLHEIRO);
        xUsuarioTipoContaRepository.saveAndFlush(relacaoTipoConta);
    }
}