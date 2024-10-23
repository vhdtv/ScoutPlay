package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsavelService {
    @Autowired
    ResponsavelRepository responsavelRepository;

    public List<Responsavel> findAll() {
        return responsavelRepository.findAll();
    }

    public Optional<Responsavel> findById(String id) {
        return responsavelRepository.findById(id);
    }

    public Responsavel save(Responsavel responsavel) {
        Optional<Responsavel> existingResponsavel = responsavelRepository.findByCpf(responsavel.getCpf());

        if (existingResponsavel.isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        return responsavelRepository.save(responsavel);
    }

    public void deleteById(String id) {
        responsavelRepository.deleteById(id);
    }
}
