package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.InteracoesDTO;
import com.scoutplay.ScoutPlay.api.dto.PostAuthorSummary;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDetailsDTO;
import com.scoutplay.ScoutPlay.api.dto.PostMediaData;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.SeguidorRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMidiaService tipoMidiaService;
    private final DetalhePerfilRepository detalhePerfilRepository;
    private final XUsuarioTipoContaRepository xUsuarioTipoContaRepository;
    private final SeguidorRepository seguidorRepository;

    @Transactional
    public PostDataOutputDTO criar(PostDataInputDTO dto) {
        String userId = SecurityUtils.currentUserId();
        Usuario autor = usuarioRepository.findByAliasId(UUID.fromString(userId)).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String arquivoSalvo = null;
        try {
            Post novoPost = new Post();
            novoPost.setTitulo(dto.getTitulo());
            novoPost.setDescricao(dto.getDescricao());
            arquivoSalvo = FileService.saveMedia(dto.getArquivo(), "uploads/media/");
            novoPost.setCaminhoArquivo(arquivoSalvo);
            novoPost.setAutor(autor);
            Set<String> imageTypes = Set.of("jpg", "webp", "png");
            if(imageTypes.contains(novoPost.obterExtensaoDaMidia())) novoPost.setTipoMidia(this.tipoMidiaService.categorizarComoImagem());
            else novoPost.setTipoMidia(this.tipoMidiaService.categorizarComoVideo());
            
            Post post = postRepository.saveAndFlush(novoPost);
            return PostDataOutputDTO.builder()
                .url(post.getAliasId())
                .titulo(post.getTitulo())
                .descricao(Optional.ofNullable(post.getDescricao()))
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
            FileService.deleteIfExists(arquivoSalvo, "uploads/media/");
            throw new IllegalStateException("Não foi possível salvar a mídia", error);
        } catch (RuntimeException error) {
            FileService.deleteIfExists(arquivoSalvo, "uploads/media/");
            throw error;
        }
    }

    @Transactional
    public PostDataOutputDTO criar(Post novoPost) {
        Post post = postRepository.saveAndFlush(novoPost);
        return PostDataOutputDTO.builder()
            .url(post.getAliasId())
            .titulo(post.getTitulo())
            .descricao(Optional.ofNullable(post.getDescricao()))
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

    @Transactional
    public List<PostDetailsDTO> listar(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new IllegalArgumentException("Paginação inválida");
        }
        Set<String> seguindoUsernames = new HashSet<>();
        try {
            String userId = SecurityUtils.currentUserId();
            if (userId != null) {
                usuarioRepository.findByAliasId(UUID.fromString(userId)).ifPresent(u ->
                    seguindoUsernames.addAll(seguidorRepository.findUsernamesSeguidos(u)));
            }
        } catch (Exception ignored) {}

        return postRepository.findByAtivoTrue(PageRequest.of(page, size, Sort.by("criadoEm").descending()))
            .getContent()
            .stream()
            .map(post -> {
                Usuario autor = post.getAutor();

                String posicao = null;
                try {
                    DetalhePerfil dp = detalhePerfilRepository.getByUsuario(autor);
                    if (dp != null && dp.getData() != null) {
                        Map<String, Object> data = dp.getData();
                        Object val = data.get("posicao");
                        if (val == null) val = data.get("POSICAO");
                        if (val != null) posicao = val.toString();
                    }
                } catch (Exception ignored) {}

                String[] tipoConta = null;
                try {
                    Set<XUsuarioTipoConta> poderes = xUsuarioTipoContaRepository.getByUsuario(autor);
                    if (poderes != null) {
                        tipoConta = poderes.stream()
                            .map(p -> switch (p.getTipoConta().getId()) {
                                case TipoConta.ATLETA -> "ATLETA";
                                case TipoConta.OLHEIRO -> "OLHEIRO";
                                case TipoConta.RESPONSAVEL -> "RESPONSAVEL";
                                case TipoConta.REPRESENTANTE_CLUBE -> "REPRESENTANTE_CLUBE";
                                default -> "USUARIO";
                            })
                            .distinct()
                            .toArray(String[]::new);
                    }
                } catch (Exception ignored) {}

                return PostDetailsDTO.builder()
                    .url(post.getAliasId())
                    .titulo(post.getTitulo())
                    .descricao(Optional.ofNullable(post.getDescricao()))
                    .media(PostMediaData.builder()
                        .src(post.getCaminhoArquivo())
                        .mimeType(post.obterMimeType())
                        .poster(null)
                        .build())
                    .criadoEm(post.getCriadoEm())
                    .interacoes(Optional.of(InteracoesDTO.builder()
                        .quantidadeLike(post.getUsuariosQueCurtiram().size())
                        .deuLike(false)
                        .build()))
                    .metadados(new PostDetailsDTO.Metadata(seguindoUsernames.contains(autor.getUsername())))
                    .autor(UserSummaryDTO.builder()
                        .nome(autor.getNome())
                        .sobrenome(autor.getSobrenome())
                        .iniciais(autor.getIniciais())
                        .username(autor.getUsername())
                        .fotoPerfil(autor.getFotoPerfil())
                        .posicao(posicao)
                        .tipoConta(tipoConta)
                        .build())
                    .build();
            })
            .collect(Collectors.toList());
    }

    public Optional<Post> buscarPor(UUID postId) {
        Optional<Post> post = this.postRepository.findByAliasIdAndAtivoTrue(postId);
        return post;
    }

    @Transactional
    public void deletar(UUID postId, UUID autorId) {
        Post post = postRepository.findByAliasIdAndAtivoTrue(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));
        if (!post.getAutor().getAliasId().equals(autorId)) {
            throw new AccessDeniedException("Sem permissão para deletar este post");
        }
        post.setAtivo(false);
        String arquivo = post.getCaminhoArquivo();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                FileService.deleteIfExists(arquivo, "uploads/media/");
            }
        });
    }

    @Transactional
    public Optional<Post> buscarPorComLike(UUID postId) {
        Optional<Post> postOpt = this.buscarPor(postId);
        postOpt.ifPresent(post -> post.getUsuariosQueCurtiram().size());
        return postOpt;
    }

}
