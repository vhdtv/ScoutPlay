package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OlheiroService {

    @Autowired
    private OlheiroRepository olheiroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Método para criar um novo olheiro
    public Olheiro criarOlheiro(Olheiro novoOlheiro) {
        if(novoOlheiro.getId() == null || novoOlheiro.getId().isEmpty()) {
            novoOlheiro.setId(novoOlheiro.gerarIdPersonalizado());
        }

        if (novoOlheiro.getSenha() == null || novoOlheiro.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para cadastro.");
        }

        olheiroRepository.findByCpf(novoOlheiro.getCpf()).ifPresent(existing -> {
            throw new ConflictException("Um olheiro com este CPF já existe.");
        });

        olheiroRepository.findByEmail(novoOlheiro.getEmail()).ifPresent(existing -> {
            throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        });

        novoOlheiro.setSenha(passwordEncoder.encode(novoOlheiro.getSenha()));

        return olheiroRepository.save(novoOlheiro);
    }

    //Método para buscar todos os olheiros (paginado via DB)
    public Page<Olheiro> buscarOlheirosPaginado(Pageable pageable) {
        return olheiroRepository.findAll(pageable);
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
        if (!olheiroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Olheiro não encontrado com ID " + id);
        }
        olheiroRepository.deleteById(id);
    }
    //Método para atualizar informações de um olheiro
    public Olheiro atualizarOlheiro(String id, Olheiro olheiroAtualizado) {
        return olheiroRepository.findById(id).map(olheiro -> {
            olheiro.setNome(olheiroAtualizado.getNome());
            olheiro.setEmail(olheiroAtualizado.getEmail());
            olheiro.setTelefone(olheiroAtualizado.getTelefone());
            olheiro.setCpf(olheiroAtualizado.getCpf());
            olheiro.setCep(olheiroAtualizado.getCep());
            olheiro.setDataNascimento(olheiroAtualizado.getDataNascimento());
            olheiro.setClube(olheiroAtualizado.getClube());
            olheiro.setLocal(olheiroAtualizado.getLocal());
            if (olheiroAtualizado.getSenha() != null && !olheiroAtualizado.getSenha().isBlank()) {
                olheiro.setSenha(passwordEncoder.encode(olheiroAtualizado.getSenha()));
            }
            return olheiroRepository.save(olheiro);

        }).orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado com ID " + id));
    }
}
