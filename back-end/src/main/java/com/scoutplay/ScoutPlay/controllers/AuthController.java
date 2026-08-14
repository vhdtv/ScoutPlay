package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.services.AuthService;
import com.scoutplay.ScoutPlay.services.PasswordResetService;
import com.scoutplay.ScoutPlay.services.UsuarioService;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import java.time.LocalDate;
import java.time.Duration;
import java.util.UUID;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService loginService;
    private final PasswordResetService passwordResetService;
    private final UsuarioService usuarioService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> login(@Valid @RequestBody ClientLoginInputDTO request) {
        ClientLoginOutputDTO loginResponse = loginService.autenticarUsuario(request);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(Duration.ofMillis(loginResponse.getExpiraEm()))
            .sameSite(cookieSameSite)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout() {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", null)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(0) // pede ao cliente para remover o cookie de autenticação
            .sameSite(cookieSameSite)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(true));
    }

    @GetMapping("/session")
    public ResponseEntity<ApiResponse<Map<String, Object>>> session(Authentication authentication) {
        Usuario usuario = usuarioService.buscarPor(UUID.fromString(authentication.getName()));
        Map<String, Object> session = Map.of(
            "username", usuario.getUsername(),
            "roles", authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .toList()
        );
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> signup(@Valid @RequestBody SignupRequest request) {
        LocalDate dataNascimento = request.getDataNascimento();
        String sobrenome = request.getSobrenome() != null ? request.getSobrenome() : "";
        Usuario usuario = new Usuario(
            request.getNome().trim(),
            sobrenome.trim(),
            request.getEmail().trim().toLowerCase(java.util.Locale.ROOT),
            request.getSenha(),
            dataNascimento
        );
        if (request.getCpf() != null && !request.getCpf().isBlank()) usuario.setCpf(request.getCpf());
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) usuario.setTelefone(request.getTelefone());

        Usuario savedUser;
        if ("OLHEIRO".equalsIgnoreCase(request.getTipoConta())) {
            savedUser = usuarioService.cadastrarOlheiro(usuario);
        } else if ("RESPONSAVEL".equalsIgnoreCase(request.getTipoConta())) {
            savedUser = usuarioService.cadastrarResponsavel(usuario);
        } else {
            savedUser = usuarioService.cadastrarAtleta(usuario);
        }

        HashMap<String, Object> profileData = new HashMap<>();
        if (request.getPeso() != null) profileData.put("peso", request.getPeso());
        if (request.getAltura() != null) profileData.put("altura", request.getAltura());
        if (request.getPosicao() != null && !request.getPosicao().isBlank()) profileData.put("posicao", request.getPosicao());
        if (request.getPeDominante() != null && !request.getPeDominante().isBlank()) profileData.put("peDominante", request.getPeDominante());
        if (request.getClubesAnteriores() != null && !request.getClubesAnteriores().isBlank()) profileData.put("clubesAnteriores", request.getClubesAnteriores());
        if (request.getCep() != null && !request.getCep().isBlank()) profileData.put("cep", request.getCep());
        if (!profileData.isEmpty()) usuarioService.adicionarInformacao(profileData, savedUser);

        ClientLoginInputDTO loginInput = ClientLoginInputDTO.builder()
            .email(request.getEmail())
            .senha(request.getSenha())
            .build();
        ClientLoginOutputDTO loginResponse = loginService.autenticarUsuario(loginInput);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true).secure(cookieSecure).path("/")
            .maxAge(Duration.ofMillis(loginResponse.getExpiraEm())).sameSite(cookieSameSite).build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Conta criada com sucesso"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.solicitarRecuperacao(request.getEmail());
        } catch (RuntimeException ex) {
            log.warn("Não foi possível iniciar recuperação de senha", ex);
        }
        return ResponseEntity.ok(ApiResponse.success(null,
                "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok(ApiResponse.success(null, "Senha redefinida com sucesso."));
    }

    @Data
    static class SignupRequest {
        @NotBlank @Size(max = 80)
        private String nome;
        @Size(max = 100)
        private String sobrenome;
        @NotBlank @Email @Size(max = 254)
        private String email;
        @NotBlank @Size(min = 8, max = 72)
        private String senha;
        @Past
        private LocalDate dataNascimento;
        @NotBlank
        @Pattern(regexp = "(?i)ATLETA|OLHEIRO|RESPONSAVEL", message = "Tipo de conta inválido")
        private String tipoConta;
        @Pattern(regexp = "^$|\\d{11}$", message = "CPF deve conter 11 dígitos")
        private String cpf;
        @Size(max = 20)
        private String telefone;
        @Pattern(regexp = "^$|\\d{8}$", message = "CEP deve conter 8 dígitos")
        private String cep;
        private Double peso;
        private Double altura;
        private String posicao;
        private String peDominante;
        private String clubesAnteriores;
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

        @NotBlank @Size(min = 8, max = 72)
        private String novaSenha;
    }

}
