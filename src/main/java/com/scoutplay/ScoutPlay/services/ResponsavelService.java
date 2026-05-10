package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponsavelService {

    @Autowired
    ResponsavelRepository responsavelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Page<Responsavel> findAllPaginated(Pageable pageable) {
        return responsavelRepository.findAll(pageable);
    }

    public Optional<Responsavel> findById(String id) {
        return responsavelRepository.findById(id);
    }

    public Responsavel save(Responsavel responsavel) {
        if (responsavelRepository.findByCpf(responsavel.getCpf()).isPresent()) {
            throw new ConflictException("CPF já cadastrado.");
        }
        if (responsavelRepository.findByEmail(responsavel.getEmail()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado.");
        }
        if (responsavel.getSenha() == null || responsavel.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para cadastro.");
        }
        responsavel.setSenha(passwordEncoder.encode(responsavel.getSenha()));
        return responsavelRepository.save(responsavel);
    }

    public Responsavel atualizar(String id, Responsavel atualizado) {
        return responsavelRepository.findById(id).map(responsavel -> {
            if (atualizado.getNome() != null) responsavel.setNome(atualizado.getNome());
            if (atualizado.getTelefone() != null) responsavel.setTelefone(atualizado.getTelefone());
            if (atualizado.getCep() != null) responsavel.setCep(atualizado.getCep());
            if (atualizado.getEmail() != null) responsavel.setEmail(atualizado.getEmail());
            if (atualizado.getSenha() != null && !atualizado.getSenha().isBlank()) {
                responsavel.setSenha(passwordEncoder.encode(atualizado.getSenha()));
            }
            return responsavelRepository.save(responsavel);
        }).orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com ID " + id));
    }

    public void deleteById(String id) {
        if (!responsavelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Responsável não encontrado com ID " + id);
        }
        responsavelRepository.deleteById(id);
    }
}
