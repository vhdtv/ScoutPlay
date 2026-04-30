package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private OlheiroRepository olheiroRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Atleta>  atleta = atletaRepository.findByEmail(email);
        if (atleta.isPresent() && senhaConfere(senha, atleta.get().getSenha())) {
            return Optional.of(atleta.get());
        }

        Optional<Olheiro> olheiro = olheiroRepository.findByEmail(email);
        if (olheiro.isPresent() && senhaConfere(senha, olheiro.get().getSenha())) {
            return Optional.of(olheiro.get());
        }

        Optional<Responsavel> responsavel = responsavelRepository.findByEmail(email);
        if (responsavel.isPresent() && senhaConfere(senha, responsavel.get().getSenha())) {
            return Optional.of(responsavel.get());
        }

        return Optional.empty();

    }

    private boolean senhaConfere(String senhaInformada, String senhaArmazenada) {
        if (senhaArmazenada == null || senhaArmazenada.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(senhaInformada, senhaArmazenada);
    }
}
