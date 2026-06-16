package com.scoutplay.ScoutPlay.services;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<Post> buscarFeed(UUID aliasId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByAliasIdWithContasQueSegue(aliasId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Set<Usuario> contasQueSegue = usuario.getContasQueSegue();
        if (contasQueSegue.isEmpty()) return Page.empty(pageable);
        return postRepository.findAllByAutorInAndAtivoTrue(contasQueSegue, pageable);
    }
}