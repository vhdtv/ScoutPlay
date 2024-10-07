package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Atleta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    //Metódo para salvar um atleta
    public Atleta salvar(Atleta atleta) {
        return atletaRepository.save(atleta);
    }

    //Metódo para buscar todos atletas
    public List<Atleta> buscarTodosAtletas() {
        return atletaRepository.findAll();
    }

    //Metódo para buscar atleta por ID
    public Optional<Atleta> buscarAtletaPorId(UUID id) {
        return atletaRepository.findById(id);
    }

    //Metódo para deletar atleta pelo ID
    public void deletarAtletaPorId(UUID id) {
        atletaRepository.deleteById(id);
    }

    //Metódo para atualizar informações de um atleta
    public Atleta atualizarAtleta(UUID id, Atleta atletaAtualizado) {
        return atletaRepository.findById(id).map(atleta -> {
            atleta.setPosicao(atletaAtualizado.getPosicao());
            atleta.setPeso(atletaAtualizado.getPeso());
            atleta.setAltura(atletaAtualizado.getAltura());
            atleta.setClubesAnteriores(atletaAtualizado.getClubesAnteriores());
            atleta.setFotoPerfil(atletaAtualizado.getFotoPerfil());
            atleta.setPeDominante(atletaAtualizado.getPeDominante());
            return atletaRepository.save(atleta);
        }).orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID" + id));
        }
    }
