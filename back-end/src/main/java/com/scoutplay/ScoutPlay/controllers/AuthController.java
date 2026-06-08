package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.services.AuthService;
import com.scoutplay.ScoutPlay.services.PasswordResetService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService loginService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> login(@Valid @RequestBody ClientLoginInputDTO request) {
        ClientLoginOutputDTO loginResponse = loginService.autenticarUsuario(request);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(loginResponse.getExpiraEm())
            .sameSite("Lax")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout() {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", null)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0) // pede ao cliente para remover o cookie de autenticação
            .sameSite("Lax")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(true));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.gerarToken(request.getEmail());
        // Em produção o token seria enviado por e-mail. Em dev retornamos no body.
        if (token != null) {
            return ResponseEntity.ok(ApiResponse.success(
                    Map.of("resetToken", token),
                    "Token gerado. Em produção seria enviado por e-mail."));
        }
        // Resposta genérica para não vazar quais e-mails existem
        return ResponseEntity.ok(ApiResponse.success(null,
                "Se o e-mail estiver cadastrado, você receberá as instruções em breve."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok(ApiResponse.success(null, "Senha redefinida com sucesso"));
    }

    @Data
    static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;
    }

    @Data
    static class ResetPasswordRequest {
        @NotBlank
        private String token;
        @NotBlank @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String novaSenha;
    }
}
