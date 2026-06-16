package com.scoutplay.ScoutPlay.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scoutplay.ScoutPlay.api.dto.InteracoesDTO;
import com.scoutplay.ScoutPlay.api.dto.MetadataDTO;
import com.scoutplay.ScoutPlay.api.dto.PostDetailsDTO;
import com.scoutplay.ScoutPlay.api.dto.PostMediaData;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.ComentarioService;
import com.scoutplay.ScoutPlay.services.FeedService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final UsuarioService usuarioService;
    private final ComentarioService comentarioService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostDetailsDTO>>> obter(@CookieValue(name = "access_token", required = true) String accessToken, @RequestParam(defaultValue = "0") Integer page) {
        Page<PostDetailsDTO> result = Page.empty();
        try {
            Usuario usuarioLogado = usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
            Page<Post> pages = feedService.buscarFeed(usuarioLogado.getAliasId(), PageRequest.of(page, 30, Sort.by("criadoEm").descending()));
            result = pages.map(item -> {
                Usuario autor = item.getAutor();
                return PostDetailsDTO.builder()
                    .url(item.getAliasId())
                    .titulo(item.getTitulo())
                    .descricao(Optional.of(item.getDescricao()))
                    .interacoes(Optional.of(InteracoesDTO.builder()
                        .destaques(null)
                        .quantidadeLike(item.getUsuariosQueCurtiram().size())
                        .quantidadeComentarios(comentarioService.buscarTodosPorPost(item).size())
                        .deuLike(item.getUsuariosQueCurtiram().stream().anyMatch(usuarioQueCurtiu -> usuarioQueCurtiu.getAliasId().equals(usuarioLogado.getAliasId())))
                        .build()))
                    .media(PostMediaData.builder()
                        .src(item.getCaminhoArquivo())
                        .poster(null)
                        .mimeType(item.obterMimeType())
                        .build())
                    .criadoEm(item.getCriadoEm())
                    .autor(UserSummaryDTO.builder()
                        .nome(autor.getNome())
                        .sobrenome(autor.getSobrenome())
                        .username(autor.getUsername())
                        .iniciais(autor.getIniciais())
                        .fotoPerfil(autor.getFotoPerfil())
                        .build())
                    .metadados(MetadataDTO.builder()
                        .segueConta(usuarioLogado.getContasQueSegue().contains(item.getAutor()))
                        .build())
                    .build();
            });
        }
        catch(Exception e) {
            System.out.println("FeedController: " + e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
