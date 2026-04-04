package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.LoginRequest;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Autentica usuário e retorna JWT token
     * Request: { "email": "user@test.com", "senha": "password123" }
     * Response: { "success": true, "data": { "token": "...", "userId": "...", ... } }
     *
     * @param loginRequest credenciais do usuário
     * @return resposta com JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<Usuario> usuario = loginService.autenticar(loginRequest.getEmail(), loginRequest.getSenha());

            if (usuario.isPresent()) {
                LoginResponse response = construirLoginResponse(usuario.get());
                return ResponseEntity.ok(ApiResponse.success(response, "Login realizado com sucesso"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("UNAUTHORIZED", "Email ou senha inválidos"));

        } catch (Exception e) {
            log.error("Erro ao realizar login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("LOGIN_ERROR", "Erro ao processar login"));
        }
    }

    /**
     * Constrói resposta de login com JWT token
     */
    private LoginResponse construirLoginResponse(Usuario usuario) {
        String userType;

        if (usuario instanceof Atleta) {
            userType = "ATLETA";
        } else if (usuario instanceof Olheiro) {
            userType = "OLHEIRO";
        } else {
            userType = "RESPONSAVEL";
        }

        // Gera JWT token
        String token = jwtTokenProvider.generateToken(usuario.getId(), userType);

        return LoginResponse.builder()
            .token(token)
            .userId(usuario.getId())
            .userType(userType)
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .build();
    }
}

