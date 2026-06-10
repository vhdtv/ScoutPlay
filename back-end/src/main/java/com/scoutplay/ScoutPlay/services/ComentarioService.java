package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.repositories.ComentarioRepository;
import com.scoutplay.ScoutPlay.repositories.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PostRepository postRepository;

    @Transactional
    public Comentario criar(Comentario novoComentario) {
        Comentario comentario = this.comentarioRepository.saveAndFlush(novoComentario);
        return comentario;
    }

    public ArrayList<Comentario> buscarTodos(UUID postId) {
        return this.comentarioRepository.findAllByPost(this.postRepository.findByAliasIdAndAtivoTrue(postId).get());
    }

    public ArrayList<Comentario> buscarTodosPorPost(Post post) {
        return this.comentarioRepository.findAllByPost(post);
    }
}
