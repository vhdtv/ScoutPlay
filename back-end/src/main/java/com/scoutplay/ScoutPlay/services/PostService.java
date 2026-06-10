package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.PostAuthorSummary;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public Optional<Post> buscarPor(UUID postId) {
        Optional<Post> post = this.postRepository.findByAliasIdAndAtivoTrue(postId);
        return post;
    }

    @Transactional
    public Optional<Post> buscarPorComLike(UUID postId) {
        Optional<Post> postOpt = this.buscarPor(postId);
        postOpt.ifPresent(post -> post.getUsuariosQueCurtiram().size());
        return postOpt;
    }

}
