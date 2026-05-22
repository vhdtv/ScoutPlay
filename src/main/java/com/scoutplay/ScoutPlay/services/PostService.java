package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.PostDTO;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PostDTO criar(PostDTO dto) {
        String userId = SecurityUtils.currentUserId();
        Usuario autor = usuarioRepository.findByAliasId(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Post post = new Post();
        post.setTitulo(dto.getTitulo());
        post.setDescricao(dto.getDescricao());
        post.setCaminhoArquivo(dto.getCaminhoArquivo());
        post.setAutor(autor);

        return toDTO(postRepository.save(post));
    }

    public PageResponse<PostDTO> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        Page<PostDTO> resultado = postRepository.findByAtivoTrue(pageable).map(this::toDTO);
        return PageResponse.fromPage(resultado);
    }

    public PageResponse<PostDTO> listarPorAutor(UUID autorId, int page, int size) {
        Usuario autor = usuarioRepository.findByAliasId(autorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        Page<PostDTO> resultado = postRepository.findByAutorAndAtivoTrue(autor, pageable).map(this::toDTO);
        return PageResponse.fromPage(resultado);
    }

    public PostDTO buscar(UUID id) {
        Post post = resolverPost(id);
        return toDTO(post);
    }

    @Transactional
    public PostDTO atualizar(UUID id, PostDTO dto) {
        Post post = resolverPost(id);
        verificarDono(post);

        if (dto.getTitulo() != null) post.setTitulo(dto.getTitulo());
        if (dto.getDescricao() != null) post.setDescricao(dto.getDescricao());
        if (dto.getCaminhoArquivo() != null) post.setCaminhoArquivo(dto.getCaminhoArquivo());

        return toDTO(postRepository.save(post));
    }

    @Transactional
    public void deletar(UUID id) {
        Post post = resolverPost(id);
        verificarDono(post);
        post.setAtivo(false);
        postRepository.save(post);
    }

    // -------------------------------------------------------------------------

    private Post resolverPost(UUID aliasId) {
        return postRepository.findByAliasIdAndAtivoTrue(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));
    }

    private void verificarDono(Post post) {
        String autorId = post.getAutor().getAliasId().toString();
        if (!SecurityUtils.isOwner(autorId)) {
            throw new AccessDeniedException("Você não tem permissão para modificar este post");
        }
    }

    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .id(post.getAliasId())
                .titulo(post.getTitulo())
                .descricao(post.getDescricao())
                .caminhoArquivo(post.getCaminhoArquivo())
                .tipoMidia(post.getTipoMidia() != null ? post.getTipoMidia().getNome() : null)
                .autorId(post.getAutor().getAliasId())
                .autorNome(post.getAutor().getNome())
                .criadoEm(post.getCriadoEm())
                .build();
    }
}
