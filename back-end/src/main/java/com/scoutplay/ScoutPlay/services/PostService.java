package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.PostAuthorSummary;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
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

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMidiaService tipoMidiaService;


    @Transactional
    public PostDataOutputDTO criar(PostDataInputDTO dto) {
        String userId = SecurityUtils.currentUserId();
        Usuario autor = usuarioRepository.findByAliasId(UUID.fromString(userId)).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        try {
            Post novoPost = new Post();
            novoPost.setTitulo(dto.getTitulo());
            novoPost.setDescricao(dto.getDescricao());
            novoPost.setCaminhoArquivo(FileService.saveFileInFolder(dto.getArquivo(), "uploads/media/"));
            novoPost.setAutor(autor);
            Set<String> imageTypes = Set.of("jpg", "webp", "png");
            if(imageTypes.contains(novoPost.obterExtensaoDaMidia())) novoPost.setTipoMidia(this.tipoMidiaService.categorizarComoImagem());
            else novoPost.setTipoMidia(this.tipoMidiaService.categorizarComoVideo());
            
            Post post = postRepository.saveAndFlush(novoPost);
            return PostDataOutputDTO.builder()
                .url(post.getAliasId())
                .titulo(post.getTitulo())
                .descricao(Optional.of(post.getDescricao()))
                .src(post.getCaminhoArquivo())
                .poster(null)
                .criadoEm(post.getCriadoEm())
                .tipoMidia(post.getTipoMidia())
                .autor(
                    PostAuthorSummary.builder()
                        .fotoPerfil(post.getAutor().getFotoPerfil())
                        .iniciais(post.getAutor().getIniciais())
                        .nome(post.getAutor().getNome())
                        .sobrenome(post.getAutor().getSobrenome())
                        .username(post.getAutor().getUsername())
                        .build()
                )
                .build();
        }
        catch(IOException error) {
            return null;
        }
    }

    @Transactional
    public PostDataOutputDTO criar(Post novoPost) {
        Post post = postRepository.saveAndFlush(novoPost);
        return PostDataOutputDTO.builder()
            .url(post.getAliasId())
            .titulo(post.getTitulo())
            .descricao(Optional.of(post.getDescricao()))
            .src(post.getCaminhoArquivo())
            .poster(null)
            .criadoEm(post.getCriadoEm())
            .tipoMidia(post.getTipoMidia())
            .autor(
                PostAuthorSummary.builder()
                    .fotoPerfil(post.getAutor().getFotoPerfil())
                    .iniciais(post.getAutor().getIniciais())
                    .nome(post.getAutor().getNome())
                    .sobrenome(post.getAutor().getSobrenome())
                    .username(post.getAutor().getUsername())
                    .build()
            )
            .build();
    }

    // public PageResponse<PostDTO> listar(int page, int size) {
    //     Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
    //     Page<PostDTO> resultado = postRepository.findByAtivoTrue(pageable).map(this::toDTO);
    //     return PageResponse.fromPage(resultado);
    // }

    // public PageResponse<PostDTO> listarPorAutor(UUID autorId, int page, int size) {
    //     Usuario autor = usuarioRepository.findByAliasId(autorId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    //     Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
    //     Page<PostDTO> resultado = postRepository.findByAutorAndAtivoTrue(autor, pageable).map(this::toDTO);
    //     return PageResponse.fromPage(resultado);
    // }

    public Optional<Post> buscarPor(UUID postId) {
        Optional<Post> post = this.postRepository.findByAliasIdAndAtivoTrue(postId);
        return post;
    }

    // @Transactional
    // public PostDTO atualizar(UUID id, PostDTO dto) {
    //     Post post = resolverPost(id);
    //     verificarDono(post);

    //     if (dto.getTitulo() != null) post.setTitulo(dto.getTitulo());
    //     if (dto.getDescricao() != null) post.setDescricao(dto.getDescricao());
    //     if (dto.getCaminhoArquivo() != null) post.setCaminhoArquivo(dto.getCaminhoArquivo());

    //     return toDTO(postRepository.save(post));
    // }

    // @Transactional
    // public void deletar(UUID id) {
    //     Post post = resolverPost(id);
    //     verificarDono(post);
    //     post.setAtivo(false);
    //     postRepository.save(post);
    // }

    // // -------------------------------------------------------------------------

    // private Post resolverPost(UUID aliasId) {
    //     return postRepository.findByAliasIdAndAtivoTrue(aliasId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));
    // }

    // private void verificarDono(Post post) {
    //     String autorId = post.getAutor().getAliasId().toString();
    //     if (!SecurityUtils.isOwner(autorId)) {
    //         throw new AccessDeniedException("Você não tem permissão para modificar este post");
    //     }
    // }

    // private PostDTO toDTO(Post post) {
    //     return PostDTO.builder()
    //             .id(post.getAliasId())
    //             .titulo(post.getTitulo())
    //             .descricao(post.getDescricao())
    //             .caminhoArquivo(post.getCaminhoArquivo())
    //             .tipoMidia(post.getTipoMidia() != null ? post.getTipoMidia().getNome() : null)
    //             .autorId(post.getAutor().getAliasId())
    //             .autorNome(post.getAutor().getNome())
    //             .criadoEm(post.getCriadoEm())
    //             .build();
    // }
}
