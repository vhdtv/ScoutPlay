package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    //Metódo para criar um novo atleta
    public Atleta criarAtleta(Atleta novoAtleta) {
        if(novoAtleta.getId() == null || novoAtleta.getId().isEmpty()){
            novoAtleta.setId(novoAtleta.gerarIdPersonalizado());
        }
        return atletaRepository.save(novoAtleta);
    }

    //Metódo para buscar todos atletas
    public List<Atleta> buscarTodosAtletas() {
        return atletaRepository.findAll();
    }

    //Metódo para buscar atleta por ID
    public Optional<Atleta> buscarAtletaPorId(String id) {
        return atletaRepository.findById(id);
    }

    //Metódo para deletar atleta pelo ID
    public void deletarAtletaPorId(String id) {
        atletaRepository.deleteById(id);
    }

    //Metódo para atualizar informações de um atleta
    public Atleta atualizarAtleta(String id, Atleta atletaAtualizado) {
        return atletaRepository.findById(id).map(atleta -> {
            atleta.setPosicao(atletaAtualizado.getPosicao());
            atleta.setPeso(atletaAtualizado.getPeso());
            atleta.setAltura(atletaAtualizado.getAltura());
            atleta.setClubesAnteriores(atletaAtualizado.getClubesAnteriores());
            atleta.setFotoPerfil(atletaAtualizado.getFotoPerfil());
            atleta.setPeDominante(atletaAtualizado.getPeDominante());
            atleta.setTelefone(atletaAtualizado.getTelefone());
            return atletaRepository.save(atleta);
        }).orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID" + id));
        }
    }
