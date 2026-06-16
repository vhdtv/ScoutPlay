package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.InteracoesDTO;
import com.scoutplay.ScoutPlay.api.dto.MetadataDTO;
import com.scoutplay.ScoutPlay.api.dto.InteracoesDTO;
import com.scoutplay.ScoutPlay.api.dto.MetadataDTO;
import com.scoutplay.ScoutPlay.api.dto.PostAuthorSummary;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDetailsDTO;
import com.scoutplay.ScoutPlay.api.dto.PostHighlightDTO;
import com.scoutplay.ScoutPlay.api.dto.PostMediaData;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final InteractionsService interactionsService;
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

    @Transactional
    public PostDetailsDTO obterPostComDetalhes(UUID postId, UUID aliasIdDoUsuarioLogado) throws Exception {
        
        Usuario usuarioLogado = usuarioRepository.findByAliasIdWithContasQueSegue(aliasIdDoUsuarioLogado).get();
        Optional<Post> linha = postRepository.findByAliasIdAndAtivoTrue(postId);
        if(linha.isEmpty()) throw new Exception(String.format("Post não encontrado: %s", postId.toString()));
        
        Post post = linha.get();
        ArrayList<PostHighlightDTO> destaquesDoPost = interactionsService.obterDestaquesDoPost(post.getAliasId(), aliasIdDoUsuarioLogado).stream().collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return PostDetailsDTO.builder()
            .url(post.getAliasId())
            .titulo(post.getTitulo())
            .descricao(Optional.of(post.getDescricao()))
            .interacoes(Optional.of(InteracoesDTO.builder()
                .quantidadeLike(post.getUsuariosQueCurtiram().size())
                .destaques(destaquesDoPost)
                .deuLike(post.getUsuariosQueCurtiram().stream().anyMatch(usuarioQueCurtiu -> usuarioQueCurtiu.getAliasId().equals(usuarioLogado.getAliasId())))
                .build()))
            .media(PostMediaData.builder()
                .src(post.getCaminhoArquivo())
                .mimeType(post.obterMimeType())
                .poster(null)
                .build())
            .criadoEm(post.getCriadoEm())
            .autor(UserSummaryDTO.builder()
                .nome(post.getAutor().getNome())
                .sobrenome(post.getAutor().getSobrenome())
                .iniciais(post.getAutor().getIniciais())
                .username(post.getAutor().getUsername())
                .fotoPerfil(post.getAutor().getFotoPerfil())
                .build())
            .metadados(MetadataDTO.builder()
                .segueConta(usuarioLogado.getContasQueSegue().contains(post.getAutor()))
                .build())
            .build();
    }

    @Transactional
    public PostDetailsDTO obterPostComDetalhes(UUID postId) throws Exception {
        
        Optional<Post> linha = postRepository.findByAliasIdAndAtivoTrue(postId);
        if(linha.isEmpty()) throw new Exception(String.format("Post não encontrado: %s", postId.toString()));

        Post post = linha.get();

        boolean deuLike = false;
        boolean segueConta = false;
        ArrayList<PostHighlightDTO> destaquesDoPost = interactionsService.obterDestaquesDoPost(post.getAliasId()).stream().map(item -> PostHighlightDTO.builder()
            .aliasId(item.getAliasId())
            .nome(item.getNome())
            .build()
        ).collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return PostDetailsDTO.builder()
            .url(post.getAliasId())
            .titulo(post.getTitulo())
            .descricao(Optional.of(post.getDescricao()))
            .interacoes(Optional.of(InteracoesDTO.builder()
                .quantidadeLike(post.getUsuariosQueCurtiram().size())
                .deuLike(deuLike)
                .destaques(destaquesDoPost)
                .build()))
            .media(PostMediaData.builder()
                .src(post.getCaminhoArquivo())
                .mimeType(post.obterMimeType())
                .poster(null)
                .build())
            .criadoEm(post.getCriadoEm())
            .autor(UserSummaryDTO.builder()
                .nome(post.getAutor().getNome())
                .sobrenome(post.getAutor().getSobrenome())
                .iniciais(post.getAutor().getIniciais())
                .username(post.getAutor().getUsername())
                .fotoPerfil(post.getAutor().getFotoPerfil())
                .build())
            .metadados(MetadataDTO.builder()
                .segueConta(segueConta)
                .build())
            .build();
    }

    @Transactional
    public Post atualizarPost(UUID postId, PostDataInputDTO dto) throws Exception {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId).get();
        Usuario usuarioLogado = usuarioRepository.findByAliasId(UUID.fromString(SecurityUtils.currentUserId())).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if(!post.getAutor().equals(usuarioLogado)) throw new Exception("Você não criou este post");
        Optional.ofNullable(dto.getTitulo()).ifPresent(post::setTitulo);
        Optional.ofNullable(dto.getDescricao()).ifPresent(post::setDescricao);
        return post;
    }

}
