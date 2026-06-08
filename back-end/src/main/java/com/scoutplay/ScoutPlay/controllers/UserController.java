package com.scoutplay.ScoutPlay.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping("/profile-detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> registrarNovoDetalhePerfil(@Valid @RequestBody ProfileDetailInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.adicionarInformacao(body.getChave(), body.getValor(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }

    @DeleteMapping("/profile-detail")
    public ResponseEntity<ApiResponse<DetalhePerfilOutputDTO>> deletarDetalhePerfil(@Valid @RequestBody ProfileDetailRemoveInputDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.removerInformacao(body.getChave(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(
            DetalhePerfilOutputDTO.builder()
                .data(registro.getData())
                .id(registro.getAliasId())
                .userId(registro.getUsuario().getAliasId())
                .build()
        ));
    }
    
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserProfileSummary>> getUserData(@RequestParam String user, @CookieValue(name = "access_token", required = false) String accessToken) throws IllegalArgumentException {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        UserProfileSummary userData = usuarioService.buscarDadosPerfil(user, aliasId);
        return ResponseEntity.ok(ApiResponse.success(userData));
    }

    
    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<DetalhePerfil>> atualizarParcialmente(@Valid @RequestBody UserProfileFieldsDTO body, @CookieValue(name = "access_token", required = true) String accessToken) {
        UUID aliasId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        DetalhePerfil registro = this.usuarioService.atualizarConfiguracoesPerfilParcialmente(body.getConfig().get(), this.usuarioService.buscarPor(aliasId));
        return ResponseEntity.ok(ApiResponse.success(registro));
    }

}
