package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.repositories.ComentarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Transactional
    public Comentario criar(Comentario novoComentario) {
        Comentario comentario = this.comentarioRepository.saveAndFlush(novoComentario);
        return comentario;
    }
}
