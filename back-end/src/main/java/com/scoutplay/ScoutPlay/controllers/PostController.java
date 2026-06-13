package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.CommentDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.CommentDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostAuthorSummary;
import com.scoutplay.ScoutPlay.api.dto.PostDataInputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDetailsDTO;
import com.scoutplay.ScoutPlay.api.dto.PostHighlightDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Destaque;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.ComentarioService;
import com.scoutplay.ScoutPlay.services.DestaqueService;
import com.scoutplay.ScoutPlay.services.InteractionsService;
import com.scoutplay.ScoutPlay.services.PostService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final DestaqueService destaqueService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService usuarioService;
    private final ComentarioService comentarioService;
    private final InteractionsService interactionsService;


    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostDetailsDTO>> buscar(@PathVariable UUID postId, @CookieValue(name = "access_token", required = false) String accessToken) {
        try {
            PostDetailsDTO result;
            if(accessToken != null) {
                result = postService.obterPostComDetalhes(postId, UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
            }
            else result = postService.obterPostComDetalhes(postId);
            return ResponseEntity.ok(ApiResponse.success(result));
        }
        catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.ok(ApiResponse.error("404", ""));
        }
    }

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostDataOutputDTO>> criar(@ModelAttribute PostDataInputDTO dto) {
        PostDataOutputDTO criado = this.postService.criar(dto);
        if(criado == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("400", "Erro ao criar post"));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(criado, "Post criado com sucesso"));
    }

    @PatchMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostDataOutputDTO>> atualizar(@RequestBody PostDataInputDTO dto, @PathVariable UUID postId) {
        try {
            Post result = postService.atualizarPost(postId, dto);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(PostDataOutputDTO.builder()
                .autor(PostAuthorSummary.builder()
                    .nome(result.getAutor().getNome())
                    .sobrenome(result.getAutor().getSobrenome())
                    .iniciais(result.getAutor().getIniciais())
                    .username(result.getAutor().getUsername())
                    .fotoPerfil(result.getAutor().getFotoPerfil())
                    .build())
                .url(result.getAliasId())
                .titulo(result.getTitulo())
                .descricao(Optional.of(result.getDescricao()))
                .src(result.getCaminhoArquivo())
                .poster(null)
                .criadoEm(result.getCriadoEm())
                .tipoMidia(result.getTipoMidia())
                .build()));
        }
        catch(Exception e) {
            return ResponseEntity.ok(ApiResponse.error("404", ""));
        }
    }

    @GetMapping("/post/{postId}/comments")
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
    
    @PostMapping("/post/{postId}/comment")
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
    
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> darLike(@PathVariable UUID postId, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPorComLike(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        interactionsService.darLike(postId, usuario);

        return ResponseEntity.ok().body(ApiResponse.success(true));
    }
    
    @PostMapping("/post/{postId}/dislike")
    public ResponseEntity<ApiResponse<Boolean>> darDislike(@PathVariable UUID postId, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPorComLike(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        interactionsService.darDislike(postId, usuario);

        return ResponseEntity.ok().body(ApiResponse.success(true));
    }

    @PostMapping("/post/{postId}/highlight")
    public ResponseEntity<ApiResponse<PostHighlightDTO>> darDestaque(@PathVariable UUID postId, @RequestBody PostHighlightDTO dto, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        Destaque destaque;
        if(dto.getAliasId() == null) destaque = destaqueService.criar(dto);
        else destaque = destaqueService.buscarPor(dto.getAliasId());
        interactionsService.darDestaque(postId, usuario.getAliasId(), destaque.getAliasId());
        return ResponseEntity.ok().body(ApiResponse.success(PostHighlightDTO.builder()
            .aliasId(destaque.getAliasId())
            .nome(destaque.getNome())
            .build()));
    }

    @DeleteMapping("/post/{postId}/highlight")
    public ResponseEntity<ApiResponse<Boolean>> retirarDestaque(@PathVariable UUID postId, @RequestBody PostHighlightDTO dto, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuario = usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        Destaque destaque = destaqueService.buscarPor(dto.getAliasId());
        if(destaque == null) return ResponseEntity.ok().body(ApiResponse.success(false, "Destaque não encontrado"));
        try {
            interactionsService.retirarDestaque(postId, usuario.getAliasId(), dto.getAliasId());
            return ResponseEntity.ok().body(ApiResponse.success(true));
        }
        catch(Exception e) {
            return ResponseEntity.ok().body(ApiResponse.success(false));
        }
    }
}
