package com.scoutplay.ScoutPlay.services;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class InteractionsService {
    final PostRepository postRepository;
    final UsuarioRepository usuarioRepository;

    @Transactional
    public void darLike(UUID postId, Usuario usuario) {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId).orElseThrow(() -> new RuntimeException("Post não encontrado"));
        usuario.curtirPost(post);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void darDislike(UUID postId, Usuario usuario) {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId).orElseThrow(() -> new RuntimeException("Post não encontrado"));
        usuario.descurtirPost(post);
        usuarioRepository.save(usuario);
    }
}
