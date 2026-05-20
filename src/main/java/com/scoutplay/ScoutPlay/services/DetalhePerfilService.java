package com.scoutplay.ScoutPlay.services;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import jakarta.transaction.Transactional;

@Service
public class DetalhePerfilService {
    @Autowired
    DetalhePerfilRepository detalhePerfilRepository;

    @Transactional
    public DetalhePerfil adicionarInformacao(Map<String, Object> data, Usuario usuario) {
        DetalhePerfil dado = this.detalhePerfilRepository.getByUsuario(usuario);
        if(dado == null) {
            dado = new DetalhePerfil(data, usuario);
            return this.detalhePerfilRepository.save(dado);
        };
        Map<String, Object> informacoesExistentes = dado.getData();
        informacoesExistentes.putAll(data);
        return dado;
    }
    @Transactional
    public DetalhePerfil adicionarInformacao(String chave, Object data, Usuario usuario) {
        DetalhePerfil dado = this.detalhePerfilRepository.getByUsuario(usuario);
        if(dado == null) {
            dado = new DetalhePerfil(new HashMap<String, Object>(), usuario);
            dado.getData().put(chave, dado);
            return this.detalhePerfilRepository.save(dado);
        };
        Map<String, Object> informacoesExistentes = dado.getData();
        if(informacoesExistentes.get(chave) != null) return dado;
        informacoesExistentes.put(chave, data);
        return dado;
    }
    @Transactional
    public void removerInformacao(String chave, Usuario usuario) {
        DetalhePerfil dado = this.detalhePerfilRepository.getByUsuario(usuario);
        if(dado == null) return;
        dado.getData().remove(chave);
    }
}