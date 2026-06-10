package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.CommentDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.CommentDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.InteracoesDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDetailsDTO;
import com.scoutplay.ScoutPlay.api.dto.PostMediaData;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.ComentarioService;
import com.scoutplay.ScoutPlay.services.InteractionsService;
import com.scoutplay.ScoutPlay.services.PostService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService usuarioService;
    private final ComentarioService comentarioService;
    private final InteractionsService interactionsService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<PostDataOutputDTO>> criar(@ModelAttribute PostDataInputDTO dto) {
        PostDataOutputDTO criado = this.postService.criar(dto);
        if(criado == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("400", "Erro ao criar post"));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(criado, "Post criado com sucesso"));
    }
    
    @PostMapping("/{postId}/comment")
    public ResponseEntity<ApiResponse<CommentDataOutputDTO>> comentar(@PathVariable UUID postId, @RequestBody CommentDataInputDTO request, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasIdDoAutor = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        Usuario autor = usuarioService.buscarPor(aliasIdDoAutor);
        Post post = postService.buscarPor(postId).get();
        Comentario comentario = new Comentario(request.getTexto(), post, autor);
        comentario = comentarioService.criar(comentario);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CommentDataOutputDTO.builder()
            .postId(post.getAliasId())
            .texto(comentario.getTexto())
            .por(UserSummaryDTO.builder()
                .nome(autor.getNome())
                .sobrenome(autor.getSobrenome())
                .username(autor.getUsername())
                .iniciais(autor.getIniciais())
                .fotoPerfil(autor.getFotoPerfil())
                .build())
            .build()));
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> darLike(@PathVariable UUID postId, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPorComLike(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        interactionsService.darLike(postId, usuario);

        return ResponseEntity.ok().body(ApiResponse.success(true));
    }
    
    @PostMapping("/{postId}/dislike")
    public ResponseEntity<ApiResponse<Boolean>> darDislike(@PathVariable UUID postId, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPorComLike(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        interactionsService.darDislike(postId, usuario);

        return ResponseEntity.ok().body(ApiResponse.success(true));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<ArrayList<CommentDataOutputDTO>>> obterComentarios(@PathVariable UUID postId) {
        Optional<Post> item = postService.buscarPor(postId);
        if(item.isEmpty()) throw new Error("Post não encontrado");
        Post post = item.get();
        ArrayList<Comentario> comentarios = comentarioService.buscarTodosPorPost(post);
        ArrayList<CommentDataOutputDTO> output = comentarios.stream().map(comentario -> {
            Usuario autor = comentario.getAutor();
            return CommentDataOutputDTO.builder()
                .postId(postId)
                .texto(comentario.getTexto())
                .por(UserSummaryDTO.builder()
                    .nome(autor.getNome())
                    .sobrenome(autor.getSobrenome())
                    .username(autor.getUsername())
                    .iniciais(autor.getIniciais())
                    .fotoPerfil(autor.getFotoPerfil())
                    .build())
                .build();
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(output));
    }

    // @GetMapping
    // public ResponseEntity<ApiResponse<PageResponse<PostDataInputDTO>>> listar(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.listar(page, size)));
    // }

    // @GetMapping("/usuario/{autorId}")
    // public ResponseEntity<ApiResponse<PageResponse<PostDataInputDTO>>> listarPorAutor(
    //         @PathVariable UUID autorId,
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.listarPorAutor(autorId, page, size)));
    // }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailsDTO>> buscar(@PathVariable UUID postId, @CookieValue(name = "access_token", required = false) String accessToken) {
        Optional<Post> post = postService.buscarPorComLike(postId);
        if(post.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("404", "Not Found"));

        Usuario usuario;
        Boolean deuLike = false;
        if(accessToken != null) {
            usuario = usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
            deuLike = post.get().getUsuariosQueCurtiram().contains(usuario);
        }
        return ResponseEntity.ok(ApiResponse.success(PostDetailsDTO.builder()
            .url(post.get().getAliasId())
            .interacoes(Optional.of(InteracoesDTO.builder()
                .quantidadeLike(post.get().getUsuariosQueCurtiram().size())
                .deuLike(deuLike)
                .build()))
            .titulo(post.get().getTitulo())
            .descricao(Optional.of(post.get().getDescricao()))
            .media(PostMediaData.builder()
                .src(post.get().getCaminhoArquivo())
                .mimeType(post.get().obterMimeType())
                .poster(null)
                .build())
            .criadoEm(post.get().getCriadoEm())
            .autor(UserSummaryDTO.builder()
                .nome(post.get().getAutor().getNome())
                .sobrenome(post.get().getAutor().getSobrenome())
                .iniciais(post.get().getAutor().getIniciais())
                .username(post.get().getAutor().getUsername())
                .fotoPerfil(post.get().getAutor().getFotoPerfil())
                .build())
            .build()));
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<ApiResponse<PostDataInputDTO>> atualizar(
    //         @PathVariable UUID id,
    //         @RequestBody PostDataInputDTO dto) {
    //     return ResponseEntity.ok(ApiResponse.success(postService.atualizar(id, dto)));
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable UUID id) {
    //     postService.deletar(id);
    //     return ResponseEntity.ok(ApiResponse.success(null, "Post deletado com sucesso"));
    // }
}
