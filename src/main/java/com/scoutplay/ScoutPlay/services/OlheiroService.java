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

    public List<Olheiro> findAll(){
        return olheiroRepository.findAll();
    }

    public Optional<Olheiro> findById(String id){
        return olheiroRepository.findById(id);
    }
    //ALTERAR PARA CRIAR OLHEITO ASSIM COMO EM ATLETA
    public Olheiro save(Olheiro olheiro){
        return olheiroRepository.save(olheiro);
    }

    public void deleteById(String id){
        olheiroRepository.deleteById(id);

    }
}
