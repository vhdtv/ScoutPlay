package com.scoutplay.ScoutPlay.services;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.AvaliacaoRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Avaliacao criar(Usuario olheiro, String atletaUsername, Double nota, String comentario) {
        Usuario atleta = usuarioRepository.findByUsernameIgnoreCase(atletaUsername);
        if (atleta == null) throw new ResourceNotFoundException("Atleta não encontrado");
        return avaliacaoRepository.save(new Avaliacao(atleta, olheiro, nota, comentario, null));
    }

    public List<Avaliacao> buscarPorAtleta(String atletaUsername) {
        Usuario atleta = usuarioRepository.findByUsernameIgnoreCase(atletaUsername);
        if (atleta == null) return Collections.emptyList();
        return avaliacaoRepository.findByAtleta(atleta);
    }
}
