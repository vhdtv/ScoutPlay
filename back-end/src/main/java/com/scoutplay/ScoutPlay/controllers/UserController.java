package com.scoutplay.ScoutPlay.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scoutplay.ScoutPlay.api.dto.DetalhePerfilOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.ProfileDetailInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ProfileDetailRemoveInputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileFieldsDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/user/{username}/detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> registrarNovoDetalhePerfil(@PathVariable String username, @Valid @RequestBody ProfileDetailInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuarioLogado = this.usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        if(!username.equals(usuarioLogado.getUsername())) return ResponseEntity.ok(ApiResponse.error("403", ""));

        DetalhePerfil registro = this.usuarioService.adicionarInformacao(body.getChave(), body.getValor(), usuarioLogado);
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }

    @DeleteMapping("/user/{username}/detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> deletarDetalhePerfil(@PathVariable String username, @Valid @RequestBody ProfileDetailRemoveInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuarioLogado = this.usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        if(!username.equals(usuarioLogado.getUsername())) return ResponseEntity.ok(ApiResponse.error("403", ""));
        
        DetalhePerfil registro = this.usuarioService.removerInformacao(body.getChave(), usuarioLogado);
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<UserProfileSummary>> getUserData(@PathVariable String username, @CookieValue(name = "access_token", required = false) String accessToken) throws IllegalArgumentException {
        UserProfileSummary userData;
        if(accessToken == null || accessToken.isEmpty() || accessToken.isBlank()) {
            userData = usuarioService.buscarDadosPerfil(username);
        }
        else {
            UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
            userData = usuarioService.buscarDadosPerfil(username, aliasId);
        }
        return ResponseEntity.ok(ApiResponse.success(userData));
    }

    
    @PatchMapping("/user/{username}/")
    public ResponseEntity<ApiResponse<DetalhePerfil>> atualizarPerfilParcialmente(@PathVariable String username, @Valid @RequestBody UserProfileFieldsDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        Usuario usuarioLogado = this.usuarioService.buscarPor(UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        if(!username.equals(usuarioLogado.getUsername())) return ResponseEntity.ok(ApiResponse.error("403", ""));
        
        DetalhePerfil registro = this.usuarioService.atualizarConfiguracoesPerfilParcialmente(body.getConfig().get(), usuarioLogado);
        return ResponseEntity.ok(ApiResponse.success(registro));
    }
    
    @PostMapping("/user/{username}/follow")
    public ResponseEntity<ApiResponse<Boolean>> seguirConta(@PathVariable String username, @CookieValue(name = "access_token", required = true) String accessToken) {
        boolean result = this.usuarioService.seguir(username, UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @PostMapping("/user/{username}/unfollow")
    public ResponseEntity<ApiResponse<Boolean>> pararDeSeguirConta(@PathVariable String username, @CookieValue(name = "access_token", required = true) String accessToken) {
        boolean result = this.usuarioService.deixarDeSeguir(username, UUID.fromString(jwtTokenProvider.extractUserId(accessToken)));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
