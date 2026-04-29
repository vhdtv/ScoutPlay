package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsavelService {
    @Autowired
    ResponsavelRepository responsavelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Responsavel> findAll() {
        return responsavelRepository.findAll();
    }

    public Optional<Responsavel> findById(String id) {
        return responsavelRepository.findById(id);
    }

    public Responsavel save(Responsavel responsavel) {
        Optional<Responsavel> existingResponsavel = responsavelRepository.findByCpf(responsavel.getCpf());

        if (existingResponsavel.isPresent()) {
            throw new ConflictException("CPF já cadastrado.");
        }

        if (responsavel.getSenha() == null || responsavel.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para cadastro.");
        }
        responsavel.setSenha(passwordEncoder.encode(responsavel.getSenha()));

        return responsavelRepository.save(responsavel);
    }

    public void deleteById(String id) {
        if (!responsavelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Responsável não encontrado com ID " + id);
        }
        responsavelRepository.deleteById(id);
    }
}
