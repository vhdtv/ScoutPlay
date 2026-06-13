package com.scoutplay.ScoutPlay.services;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.models.Destaque;
import com.scoutplay.ScoutPlay.models.DestaquesEmPost;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.DestaqueRepository;
import com.scoutplay.ScoutPlay.repositories.DestaquesEmPostRepository;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class InteractionsService {
    final PostRepository postRepository;
    final UsuarioRepository usuarioRepository;
    final DestaqueRepository destaqueRepository;
    final DestaquesEmPostRepository destaquesEmPostRepository;

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
    
    @Transactional
    public void darDestaque(UUID postId, UUID usuarioLogadoId, UUID destaqueId) {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId).orElseThrow(() -> new RuntimeException("Post não encontrado"));
        Usuario usuario = usuarioRepository.findByAliasId(usuarioLogadoId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Optional<Destaque> destaque = destaqueRepository.findByAliasId(destaqueId);
        if(destaque.isEmpty()) new RuntimeException("Destaque não encontrado");

        if (destaquesEmPostRepository.existsByPostAndUsuarioAndDestaque(post, usuario, destaque.get())) throw new IllegalStateException("Você já adicionou esse destaque a este post.");
        DestaquesEmPost novaMarcacao = DestaquesEmPost.builder()
                .post(post)
                .usuario(usuario)
                .destaque(destaque.get())
                .build();

        destaquesEmPostRepository.save(novaMarcacao);
    }
}
