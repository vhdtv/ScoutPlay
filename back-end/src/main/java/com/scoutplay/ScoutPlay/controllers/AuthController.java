package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.services.PasswordResetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordResetService passwordResetService;

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
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
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
