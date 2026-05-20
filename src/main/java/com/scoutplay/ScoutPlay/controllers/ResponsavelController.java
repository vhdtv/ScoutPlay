package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.ResponsavelDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/responsaveis")
@RequiredArgsConstructor
public class ResponsavelController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * POST /api/responsaveis/registro
     * Cria conta de responsável e retorna token JWT.
     *
     * FRONTEND (Caio): usar este endpoint para cadastrar o responsável
     * de um atleta menor de idade. Após o registro, vincular ao atleta via
     * PUT /api/atletas/{atletaId} enviando o campo responsavelId no body
     * (a ser implementado quando necessário).
     */
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrar(@Valid @RequestBody ResponsavelDTO dto) {
        LocalDate nascimento = dto.getDataNascimento() != null
                ? dto.getDataNascimento()
                : LocalDate.of(1990, 1, 1);

        Usuario usuario = new Usuario(
                dto.getNome(), "",
                dto.getEmail(), dto.getCpf(),
                dto.getSenha(), nascimento
        );
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());

        Usuario salvo = usuarioService.cadastrarResponsavel(usuario);

        String userId = salvo.getAliasId().toString();
        String token = jwtTokenProvider.generateToken(userId, "RESPONSAVEL");

        LoginResponse response = LoginResponse.builder()
                .token(token).userId(userId).userType("RESPONSAVEL")
                .nome(salvo.getNome()).email(salvo.getEmail())
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Responsável registrado com sucesso"));
    }
}
