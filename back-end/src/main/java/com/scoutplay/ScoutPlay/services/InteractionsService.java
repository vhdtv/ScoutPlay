package com.scoutplay.ScoutPlay.services;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.api.dto.PostHighlightDTO;
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
    public List<PostHighlightDTO> obterDestaquesDoPost(UUID postId, UUID userAliasId) {
        List<PostHighlightDTO> destaquesDoPost = destaqueRepository.findAllByPostWithUserContext(postId, userAliasId).stream().map(item -> PostHighlightDTO.builder()
            .aliasId(item.getAliasId())
            .nome(item.getNome())
            .count(item.getQuantidadeMarcada())
            .marcadoPeloUsuario(item.getMarcadoPeloUsuario())
            .build()
        ).collect(Collectors.toList());
        return destaquesDoPost;
    }
    
    @Transactional
    public List<PostHighlightDTO> obterDestaquesDoPost(UUID postId) {
        List<PostHighlightDTO> destaquesDoPost = destaqueRepository.findAllByPost(postId).stream().map(item -> PostHighlightDTO.builder()
            .aliasId(item.getAliasId())
            .nome(item.getNome())
            .count(item.getQuantidadeMarcada())
            .marcadoPeloUsuario(item.getMarcadoPeloUsuario())
            .build()
        ).collect(Collectors.toList());
        return destaquesDoPost;
    }
    
    @Transactional
    public DestaquesEmPost darDestaque(UUID postId, UUID usuarioLogadoId, UUID destaqueId) {
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

        destaquesEmPostRepository.saveAndFlush(novaMarcacao);
        return novaMarcacao;
    }

    public void retirarDestaque(UUID postId, UUID usuarioLogadoId, UUID destaqueId) {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId).orElseThrow(() -> new RuntimeException("Post não encontrado"));
        Usuario usuario = usuarioRepository.findByAliasId(usuarioLogadoId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Destaque destaque = destaqueRepository.findByAliasId(destaqueId).get();
        DestaquesEmPost linhaEmBanco = destaquesEmPostRepository.findByPostAndUsuarioAndDestaque(post, usuario, destaque);
        destaquesEmPostRepository.delete(linhaEmBanco);
    }
}
