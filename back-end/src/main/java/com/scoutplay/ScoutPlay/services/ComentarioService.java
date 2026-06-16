package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
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
        return this.comentarioRepository.saveAndFlush(novoComentario);
    }

    public ArrayList<Comentario> buscarTodos(UUID postId) {
        return this.comentarioRepository.findAllByPost(this.postRepository.findByAliasIdAndAtivoTrue(postId).get());
    }

    public ArrayList<Comentario> buscarRaizPorPost(Post post) {
        return this.comentarioRepository.findAllByPostAndParentComentarioIsNull(post);
    }

    public ArrayList<Comentario> buscarTodosPorPost(Post post) {
        return this.comentarioRepository.findAllByPost(post);
    }

    public ArrayList<Comentario> buscarRespostas(Comentario parent) {
        return this.comentarioRepository.findAllByParentComentario(parent);
    }

    public Comentario buscarPorId(UUID id) {
        return this.comentarioRepository.findByAliasId(id)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
    }

    @Transactional
    public Comentario curtir(UUID comentarioId, Usuario usuario) {
        Comentario c = this.comentarioRepository.findByAliasId(comentarioId)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
        c.getCurtidas().add(usuario);
        return c;
    }

    @Transactional
    public Comentario descurtir(UUID comentarioId, Usuario usuario) {
        Comentario c = this.comentarioRepository.findByAliasId(comentarioId)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
        c.getCurtidas().remove(usuario);
        return c;
    }
}
