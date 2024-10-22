package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private OlheiroRepository olheiroRepository;

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Atleta>  atleta = atletaRepository.findByEmail(email);
        if(atleta.isPresent() && atleta.get().getSenha().equals(senha)){
            return Optional.of(atleta.get());
        }

        Optional<Olheiro> olheiro = olheiroRepository.findByEmail(email);
        if (olheiro.isPresent() && olheiro.get().getSenha().equals(senha) ){
            return Optional.of(olheiro.get());
        }
        return Optional.empty();

    }
}
