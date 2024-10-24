package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OlheiroService {

    @Autowired
    private OlheiroRepository olheiroRepository;

    //Método para criar um novo olheiro
    public Olheiro criarOlheiro(Olheiro novoOlheiro) {
        if(novoOlheiro.getId() == null || novoOlheiro.getId().isEmpty()) {
            novoOlheiro.setId(novoOlheiro.gerarIdPersonalizado());
        }
        return olheiroRepository.save(novoOlheiro);
    }
    //Método para buscar todos os olheiros
    public List<Olheiro> buscarTodosOlheiros(){
        return olheiroRepository.findAll();
    }
    //Método para buscar olheiro por id
    public Optional<Olheiro> buscarOlheiroPorId(String id){
        return olheiroRepository.findById(id);
    }

    //Metódo para deletar olheiro por id
    public void deletarOlheiroPorId(String id){
        olheiroRepository.deleteById(id);
    }
    //Método para atualizar informações de um olheiro
    public Olheiro atualizarOlheiro(String id, Olheiro olheiroAtualizado) {
        return olheiroRepository.findById(id).map(olheiro -> {
            olheiro.setNome(olheiroAtualizado.getNome());
            olheiro.setEmail(olheiroAtualizado.getEmail());
            olheiro.setTelefone(olheiroAtualizado.getTelefone());
            return olheiroRepository.save(olheiro);

        }).orElseThrow(() -> new RuntimeException("Olheiro não encontrado com ID"+ id));
    }
}
