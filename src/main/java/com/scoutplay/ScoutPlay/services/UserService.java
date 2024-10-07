package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.Repositorys.UserRepository;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //Salvar ou atualizar um usuário
    public Usuario save(Usuario usuario) {
        return userRepository.save(usuario);
    }

    //Buscar todos os usuários
    public List<Usuario> findAll() {
        return userRepository.findAll();
    }

    //Buscar um usuário pelo ID
    public Optional<Usuario> findById(String id) {
        return userRepository.findById(id);
    }

    //Deletar usuário
    public void delete(Usuario usuario) {
        userRepository.delete(usuario);
    }
}
